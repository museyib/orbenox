package com.orbenox.erp;

import com.jayway.jsonpath.JsonPath;
import com.orbenox.erp.domain.businesspartner.BusinessPartnerItem;
import com.orbenox.erp.domain.price.SimplePriceListItem;
import com.orbenox.erp.domain.transactiontype.SimpleTransactionTypeItem;
import com.orbenox.erp.domain.warehouse.WarehouseItem;
import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.exception.GlobalExceptionHandler;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.controller.ProductApproveController;
import com.orbenox.erp.transaction.controller.SalesOrderController;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.idempotency.IdempotencyExecutor;
import com.orbenox.erp.transaction.idempotency.IdempotencyService;
import com.orbenox.erp.transaction.idempotency.IdempotencyAspect;
import com.orbenox.erp.transaction.idempotency.IdempotentRecord;
import com.orbenox.erp.transaction.projection.DocumentItem;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.service.ProductApproveActionService;
import com.orbenox.erp.transaction.service.SalesOrderActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.orbenox.erp.transaction.idempotency.IdempotentRecord.Status.COMPLETED;
import static com.orbenox.erp.transaction.idempotency.IdempotentRecord.Status.PROCESSING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DocumentApiIdempotencyTest {
    private static final long DOCUMENT_ID = 101L;
    private static final String IDEMPOTENCY_KEY = "idem-001";

    @Mock
    private ProductApproveActionService productApproveActionService;
    @Mock
    private SalesOrderActionService salesOrderActionService;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private LocalizationService localizationService;

    private JsonMapper jsonMapper;
    private InMemoryIdempotencyState idempotencyState;
    private IdempotencyService idempotencyService;
    private IdempotencyExecutor idempotencyExecutor;
    private DocumentItem documentItem;

    @BeforeEach
    void setUp() {
        jsonMapper = new JsonMapper();
        idempotencyState = new InMemoryIdempotencyState();
        idempotencyService = Mockito.mock(IdempotencyService.class);
        idempotencyExecutor = Mockito.mock(IdempotencyExecutor.class);
        documentItem = new StubDocumentItem(DOCUMENT_ID, "DOC-001");

        lenient().when(idempotencyService.getRecord(anyString()))
                .thenAnswer(invocation -> idempotencyState.get(invocation.getArgument(0)));
        lenient().when(idempotencyService.tryLock(anyString(), anyString()))
                .thenAnswer(invocation -> idempotencyState.tryLock(
                        invocation.getArgument(0), invocation.getArgument(1)));
        lenient().doAnswer(invocation -> {
            idempotencyState.complete(
                    invocation.getArgument(0),
                    invocation.getArgument(1),
                    invocation.getArgument(2),
                    jsonMapper.writeValueAsString(invocation.getArgument(3)));
            return null;
        }).when(idempotencyService).complete(anyString(), anyInt(), anyString(), any());
    }

    @Test
    void documentsCreate_withoutIdempotencyKey_shouldProceed() throws Exception {
        stubSuccessfulSalesOrderCreate();
        MockMvc mockMvc = createMockMvc(new SalesOrderController(
                salesOrderActionService,
                documentRepository,
                localizationService));

        MvcResult result = mockMvc.perform(post("/api/salesOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(salesOrderCreateJson()))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(DOCUMENT_ID, responseDocumentId(result, "$.data.id"));
        verify(salesOrderActionService, times(1)).createDraft(any());
    }

    @Test
    void documentsCreate_sameKeyTwice_shouldCreateOnceAndReplayStoredBody() throws Exception {
        MockMvc mockMvc = createMockMvc(new SalesOrderController(
                salesOrderActionService,
                documentRepository,
                localizationService));
        stubSuccessfulSalesOrderCreate();

        MvcResult first = mockMvc.perform(post("/api/salesOrder")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(salesOrderCreateJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value((int) DOCUMENT_ID))
                .andReturn();

        MvcResult second = mockMvc.perform(post("/api/salesOrder")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(salesOrderCreateJson()))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(DOCUMENT_ID, responseDocumentId(first, "$.data.id"));
        assertEquals(200, second.getResponse().getStatus());
    }

    @Test
    void documentsCreate_sameKeyWithDifferentBody_shouldRejectDuplicate() throws Exception {
        MockMvc mockMvc = createMockMvc(new SalesOrderController(
                salesOrderActionService,
                documentRepository,
                localizationService));
        stubSuccessfulSalesOrderCreate();

        MvcResult first = mockMvc.perform(post("/api/salesOrder")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(salesOrderCreateJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value((int) DOCUMENT_ID))
                .andReturn();

        MvcResult second = mockMvc.perform(post("/api/salesOrder")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(documentCreateJsonWithDifferentBody()))
                .andExpect(status().isConflict())
                .andReturn();

        assertEquals(DOCUMENT_ID, responseDocumentId(first, "$.data.id"));
        assertEquals(409, second.getResponse().getStatus());
        verify(salesOrderActionService, times(1)).createDraft(any());
        verify(documentRepository, times(1)).getItemByIdAndType(DOCUMENT_ID, 2L);
        verify(idempotencyService, times(1)).complete(anyString(), anyInt(), anyString(), any());
    }

    @Test
    void documentsCreate_twentySimultaneousRequestsWithSameKey_shouldCreateOnceAndReturnNineteenConflicts() throws Exception {
        String concurrentKey = "ABC";
        int requestCount = 20;
        MockMvc mockMvc = createMockMvc(new SalesOrderController(
                salesOrderActionService,
                documentRepository,
                localizationService));
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.setDocumentNo("DOC-001");
        document.setDocumentDate(LocalDate.now());

        CountDownLatch ready = new CountDownLatch(requestCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch firstRequestEnteredCreate = new CountDownLatch(1);
        CountDownLatch releaseCreate = new CountDownLatch(1);
        CountDownLatch conflictsObserved = new CountDownLatch(requestCount - 1);

        when(salesOrderActionService.createDraft(any())).thenAnswer(invocation -> {
            firstRequestEnteredCreate.countDown();
            assertTrue(releaseCreate.await(10, TimeUnit.SECONDS), "Timed out while waiting to release createDraft");
            return document;
        });
        when(documentRepository.getItemByIdAndType(anyLong(), Mockito.eq(2L))).thenReturn(documentItem);

        try (ExecutorService executor = Executors.newFixedThreadPool(requestCount)) {
            List<Future<Integer>> futures = new ArrayList<>();
            try {
                for (int i = 0; i < requestCount; i++) {
                    futures.add(executor.submit(() -> {
                        ready.countDown();
                        assertTrue(start.await(10, TimeUnit.SECONDS), "Timed out while waiting for concurrent start");
                        int statusCode = mockMvc.perform(post("/api/salesOrder")
                                        .header("Idempotency-Key", concurrentKey)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(salesOrderCreateJson()))
                                .andReturn()
                                .getResponse()
                                .getStatus();
                        if (statusCode == 400) {
                            conflictsObserved.countDown();
                        }
                        return statusCode;
                    }));
                }

                assertTrue(ready.await(10, TimeUnit.SECONDS), "Worker threads did not become ready in time");
                start.countDown();
                assertTrue(firstRequestEnteredCreate.await(10, TimeUnit.SECONDS), "No request reached createDraft");
                assertTrue(conflictsObserved.await(10, TimeUnit.SECONDS), "Expected the remaining requests to conflict");
                releaseCreate.countDown();

                int okCount = 0;
                int conflictCount = 0;
                for (Future<Integer> future : futures) {
                    int statusCode = future.get(10, TimeUnit.SECONDS);
                    if (statusCode == 200) {
                        okCount++;
                    } else if (statusCode == 400) {
                        conflictCount++;
                    }
                }

                assertEquals(1, okCount);
                assertEquals(19, conflictCount);
            } finally {
                releaseCreate.countDown();
                executor.shutdownNow();
                assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS), "Executor did not stop in time");
            }
        }

        verify(salesOrderActionService, times(1)).createDraft(any());
        verify(documentRepository, times(1)).getItemByIdAndType(DOCUMENT_ID, 2L);
        verify(idempotencyService, times(1)).complete(anyString(), anyInt(), anyString(), any());
    }

    @Test
    void productApproveCreate_sameKeyTwice_shouldReplayCompletedResponse() throws Exception {
        MockMvc mockMvc = createMockMvc(new ProductApproveController(
                productApproveActionService,
                documentRepository,
                localizationService));
        stubSuccessfulProductApproveCreate();

        MvcResult first = mockMvc.perform(post("/api/productApproves")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productApproveCreateJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value((int) DOCUMENT_ID))
                .andReturn();

        assertEquals(200, first.getResponse().getStatus());

        MvcResult second = mockMvc.perform(post("/api/productApproves")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productApproveCreateJson()))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, second.getResponse().getStatus());

        JsonNode expected = jsonMapper.readTree(
                first.getResponse().getContentAsString()
        );

        JsonNode actual = jsonMapper.readTree(
                second.getResponse().getContentAsString()
        );

        assertEquals(expected, actual);

        verify(productApproveActionService, times(1)).createDraft(any());
    }

    @Test
    void productApproveCreate_sameKeyWithDifferentBody_shouldRejectDuplicate() throws Exception {
        MockMvc mockMvc = createMockMvc(new ProductApproveController(
                productApproveActionService,
                documentRepository,
                localizationService));
        stubSuccessfulProductApproveCreate();

        mockMvc.perform(post("/api/productApproves")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productApproveCreateJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value((int) DOCUMENT_ID));

        MvcResult second = mockMvc.perform(post("/api/productApproves")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productApproveCreateJsonWithDifferentBody()))
                .andExpect(status().isConflict())
                .andReturn();

        assertEquals(409, second.getResponse().getStatus());
        verify(productApproveActionService, times(1)).createDraft(any());
        verify(idempotencyService, times(1)).complete(anyString(), anyInt(), anyString(), any());
    }

    private MockMvc createMockMvc(Object controller) {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(controller);
        proxyFactory.addAspect(new IdempotencyAspect(idempotencyExecutor, jsonMapper));
        Object proxiedController = proxyFactory.getProxy();
        return MockMvcBuilders.standaloneSetup(proxiedController)
                .setControllerAdvice(new GlobalExceptionHandler(localizationService))
                .setMessageConverters(new JacksonJsonHttpMessageConverter(jsonMapper))
                .build();
    }

    private void stubSuccessfulSalesOrderCreate() {
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.setDocumentNo("DOC-001");
        document.setDocumentDate(LocalDate.now());

        when(salesOrderActionService.createDraft(any())).thenReturn(document);
        when(documentRepository.getItemByIdAndType(anyLong(), Mockito.eq(2L))).thenReturn(documentItem);
    }

    private void stubSuccessfulProductApproveCreate() {
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.setDocumentNo("DOC-001");
        document.setDocumentDate(LocalDate.now());

        when(productApproveActionService.createDraft(any())).thenReturn(document);
        when(documentRepository.getItemByIdAndType(anyLong(), Mockito.eq(1L))).thenReturn(documentItem);
    }

    private long responseDocumentId(MvcResult result, String jsonPath) throws Exception {
        Number id = JsonPath.read(result.getResponse().getContentAsString(), jsonPath);
        return id.longValue();
    }

    private String salesOrderCreateJson() {
        return """
                {
                  "documentDate": "2026-09-18",
                  "description": "Idempotent generic document",
                  "partnerId": 1,
                  "paymentMethod": "CASH",
                  "priceListId": 1,
                  "sourceWarehouseId": 1,
                  "lines": [
                    {
                      "productId": 1,
                      "quantity": 1,
                      "unitPrice": 1,
                      "discountRatio": 1
                    }
                  ]
                }
                """;
    }

    private String productApproveCreateJson() {
        return """
                {
                  "documentDate": "2026-09-18",
                  "description": "Idempotent product approve",
                  "paymentMethod": null,
                  "priceListId": 1,
                  "targetWarehouseId": 1,
                  "lines": [
                    {
                      "productId": 1,
                      "quantity": 1,
                      "unitPrice": 1,
                      "discountRatio": 1
                    }
                  ]
                }
                """;
    }

    private String documentCreateJsonWithDifferentBody() {
        return """
                {
                  "documentDate": "2026-09-19",
                  "description": "Idempotent generic document changed body",
                  "partnerId": 2,
                  "paymentMethod": "CARD",
                  "priceListId": 2,
                  "sourceWarehouseId": 2,
                  "lines": [
                    {
                      "productId": 2,
                      "quantity": 5,
                      "unitPrice": 9,
                      "discountRatio": 0
                    }
                  ]
                }
                """;
    }

    private String productApproveCreateJsonWithDifferentBody() {
        return """
                {
                  "documentDate": "2026-09-19",
                  "description": "Idempotent product approve changed body",
                  "paymentMethod": null,
                  "priceListId": 2,
                  "targetWarehouseId": 2,
                  "lines": [
                    {
                      "productId": 2,
                      "quantity": 5,
                      "unitPrice": 9,
                      "discountRatio": 0
                    }
                  ]
                }
                """;
    }

    private static final class InMemoryIdempotencyState {
        private final Map<String, IdempotentRecord> records = new ConcurrentHashMap<>();

        private IdempotentRecord get(String key) {
            return records.get(key);
        }

        private boolean tryLock(String key, String requestHash) {
            IdempotentRecord processing = new IdempotentRecord();
            processing.setStatus(PROCESSING);
            processing.setRequestHash(requestHash);
            return records.putIfAbsent(key, processing) == null;
        }

        private void complete(String key, int responseStatus, String requestHash, String responseBody) {
            IdempotentRecord completed = new IdempotentRecord();
            completed.setStatus(COMPLETED);
            completed.setRequestHash(requestHash);
            completed.setResponseBody(responseBody);
            completed.setResponseStatus(responseStatus);
            records.put(key, completed);
        }

        private IdempotentRecord.Status statusOf(String key) {
            IdempotentRecord record = records.get(key);
            return record == null ? null : record.getStatus();
        }
    }

    private static final class StubDocumentItem implements DocumentItem {
        private final Long id;
        private final String documentNo;

        private StubDocumentItem(Long id, String documentNo) {
            this.id = id;
            this.documentNo = documentNo;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getDocumentNo() {
            return documentNo;
        }

        @Override
        public LocalDate getDocumentDate() {
            return LocalDate.of(2026, 9, 18);
        }

        @Override
        public String getDescription() {
            return "stub";
        }

        @Override
        public DocumentStatus getDocumentStatus() {
            return DocumentStatus.DRAFT;
        }

        @Override
        public ApprovalStatus getApprovalStatus() {
            return ApprovalStatus.PENDING;
        }

        @Override
        public SimpleTransactionTypeItem getTypeItem() {
            return null;
        }

        @Override
        public Long getPartnerId() {
            return null;
        }

        @Override
        public WarehouseItem getSourceWarehouse() {
            return null;
        }

        @Override
        public WarehouseItem getTargetWarehouse() {
            return null;
        }

        @Override
        public BusinessPartnerItem getBusinessPartner() {
            return null;
        }

        @Override
        public SimplePriceListItem getPriceList() {
            return null;
        }
    }
}

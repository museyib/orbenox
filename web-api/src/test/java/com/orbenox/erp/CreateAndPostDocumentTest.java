package com.orbenox.erp;

import com.orbenox.erp.domain.businesspartner.BusinessPartnerRepository;
import com.orbenox.erp.domain.price.PriceListRepository;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.stock.StockBalance;
import com.orbenox.erp.domain.stock.StockBalanceRepository;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.domain.warehouse.WarehouseRepository;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.command.ProductLineCommand;
import com.orbenox.erp.transaction.entity.*;
import com.orbenox.erp.transaction.policy.approval.ApprovalPolicy;
import com.orbenox.erp.transaction.policy.approval.SalesOrderApprovalPolicy;
import com.orbenox.erp.transaction.repository.*;
import jakarta.persistence.EntityManager;
import com.orbenox.erp.transaction.resolver.PolicyResolver;
import com.orbenox.erp.transaction.service.DocumentActionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.orbenox.erp.enums.JournalStatus.POSTED;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class CreateAndPostDocumentTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    DocumentActionService documentActionService;
    @Autowired
    TransactionTypeRepository transactionTypeRepo;
    @Autowired
    ProductRepository productRepo;
    @Autowired
    WarehouseRepository warehouseRepo;
    @Autowired
    StockMovementRepository stockMovementRepo;
    @Autowired
    PolicyResolver<ApprovalPolicy> approvalPolicyResolver;

    private Product product;
    private Warehouse warehouse;
    private Long priceListId;

    private Long approveTypeId;
    private Long salesOrderTypeId;
    private Long partnerId;

    @Autowired
    private BusinessPartnerRepository businessPartnerRepository;
    @Autowired
    private PriceListRepository priceListRepository;
    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private JournalLineRepository journalLineRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private StockBalanceRepository stockBalanceRepo;
    @Autowired
    private EntityManager entityManager;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void createEntities() {
        approveTypeId = transactionTypeRepo.findByCode("PRODUCT_APPROVE").getId();
        salesOrderTypeId = transactionTypeRepo.findByCode("SALES_ORDER").getId();
        partnerId = businessPartnerRepository.findAll().get(0).getId();
        priceListId = priceListRepository.findAll().get(0).getId();

        product = productRepo.findAll().get(0);
        warehouse = warehouseRepo.findAll().get(0);
    }

    @Test
    @Transactional
    public void productApprove_createStockMovement() {
        ProductLineCommand lineCommand = new ProductLineCommand(
                product.getId(),
                BigDecimal.TEN,
                BigDecimal.ONE,
                BigDecimal.ZERO);
        CreateDocumentCommand cmd = new CreateDocumentCommand(
                LocalDate.now(),
                approveTypeId,
                "Test",
                null,
                null,
                priceListId,
                null,
                warehouse.getId(),
                List.of(lineCommand));

        Document document = documentActionService.createDraft(cmd);

        documentActionService.submit(document.getId());
        documentActionService.post(document.getId());

        List<StockMovement> stockMovements = stockMovementRepo.findByDocumentId(document.getId());

        assertEquals(1, stockMovements.size());
        assertEquals(BigDecimal.TEN, stockMovements.get(0).getQuantity());
        StockBalance stockBalance = stockBalanceRepo.findByProductAndWarehouse(product, warehouse).orElseGet(StockBalance::new);
        assertEquals(0, stockBalance.getQuantity().compareTo(BigDecimal.TEN));
    }

    @Test
    @Transactional
    public void salesOrder_createStockAndAccountingEntries() {
        seedStockBalance(BigDecimal.TEN);

        ProductLineCommand lineCommand = new ProductLineCommand(
                product.getId(),
                BigDecimal.TEN,
                BigDecimal.ONE,
                BigDecimal.valueOf(50.0));
        CreateDocumentCommand cmd = new CreateDocumentCommand(
                LocalDate.now(),
                salesOrderTypeId,
                "Sales order",
                partnerId,
                "CASH",
                priceListId,
                warehouse.getId(),
                null,
                List.of(lineCommand));
        Document document = documentActionService.createDraft(cmd);

        documentActionService.submit(document.getId());
        documentActionService.approve(document.getId());
        documentActionService.post(document.getId());

        assertEquals(1, stockMovementRepo.countByDocumentId(document.getId()));

        JournalEntry journalEntry = journalEntryRepository.findByDocumentId(document.getId());
        assertEquals(POSTED, journalEntry.getStatus());
        List<JournalLine> journalLines = journalLineRepository.findByJournalEntryId(journalEntry.getId());
        assertEquals(2, journalLines.size());

        assertTrue(journalLines.stream().anyMatch(l -> l.getAccount().getCode().equals("2100") && l.getDebit().compareTo(BigDecimal.ZERO) > 0));
        assertTrue(journalLines.stream().anyMatch(l -> l.getAccount().getCode().equals("3000") && l.getCredit().compareTo(BigDecimal.ZERO) > 0));

        assertEquals(0, reloadStockBalance(product, warehouse)
                .getQuantity()
                .compareTo(BigDecimal.ZERO));
        ApprovalPolicy policy = approvalPolicyResolver.resolve(document.getType());
        assertInstanceOf(SalesOrderApprovalPolicy.class, policy);
        assertTrue(document.getType().isApprovalRequired());
        assertTrue(policy.supports(document.getType()));
        assertTrue(policy.requiresApproval(document));
    }

    @Test
    public void salesOrder_postWithInsufficientStock_shouldRollbackSideEffects() {
        seedStockBalance(BigDecimal.ONE);

        ProductLineCommand lineCommand = new ProductLineCommand(
                product.getId(),
                BigDecimal.TWO,
                BigDecimal.ONE,
                BigDecimal.ONE);
        CreateDocumentCommand cmd = new CreateDocumentCommand(
                LocalDate.now(),
                salesOrderTypeId,
                "Insufficient stock",
                partnerId,
                "CASH",
                priceListId,
                warehouse.getId(),
                null,
                List.of(lineCommand));
        Document document = documentActionService.createDraft(cmd);

        documentActionService.submit(document.getId());
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> documentActionService.post(document.getId()));

        assertTrue(exception.getMessage().contains("Insufficient stock quantity"));
        assertEquals(0, stockBalanceRepo.findByProductAndWarehouse(product, warehouse).orElseThrow().getQuantity().compareTo(BigDecimal.ONE));
        assertEquals(0, stockMovementRepo.countByDocumentId(document.getId()));
        assertNull(journalEntryRepository.findByDocumentId(document.getId()));
        assertEquals(DocumentStatus.IN_PROGRESS,
                documentRepository.findById(document.getId()).orElseThrow().getDocumentStatus());
    }

    @Test
    public void salesOrders_postConcurrentlyAgainstSingleUnitOfStock_shouldLeaveConsistentState() throws Exception {
        Warehouse isolatedWarehouse = createIsolatedWarehouse("CONC-SO");
        seedStockBalance(isolatedWarehouse, BigDecimal.ONE);

        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            documents.add(createSubmittedSalesOrder(isolatedWarehouse, BigDecimal.ONE, BigDecimal.ONE));
        }

        List<PostAttemptResult> results = postConcurrently(documents);

        long successCount = results.stream().filter(PostAttemptResult::success).count();
        long movementCount = documents.stream().mapToLong(doc -> stockMovementRepo.countByDocumentId(doc.getId())).sum();
        long postedCount = documents.stream()
                .filter(doc -> documentRepository.findById(doc.getId()).orElseThrow().getDocumentStatus() == DocumentStatus.POSTED)
                .count();

        assertEquals(1, successCount);
        assertTrue(results.stream()
                .filter(result -> !result.success())
                .allMatch(this::isExpectedConcurrentSalesOrderFailure), results.toString());
        assertEquals(0, stockBalanceRepo.findByProductAndWarehouse(product, isolatedWarehouse)
                .orElseThrow()
                .getQuantity()
                .compareTo(BigDecimal.ZERO));
        assertEquals(1, movementCount);
        assertEquals(1, postedCount);
    }

    @Test
    public void productApprovals_postConcurrently_shouldKeepSuccessfulUpdatesConsistent() throws Exception {
        Warehouse isolatedWarehouse = createIsolatedWarehouse("CONC-PA");
        seedStockBalance(isolatedWarehouse, BigDecimal.ZERO);

        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            documents.add(createSubmittedProductApprove(isolatedWarehouse, BigDecimal.ONE));
        }

        List<PostAttemptResult> results = postConcurrently(documents);

        long successCount = results.stream().filter(PostAttemptResult::success).count();
        long movementCount = documents.stream().mapToLong(doc -> stockMovementRepo.countByDocumentId(doc.getId())).sum();
        long postedCount = documents.stream()
                .filter(doc -> documentRepository.findById(doc.getId()).orElseThrow().getDocumentStatus() == DocumentStatus.POSTED)
                .count();

        assertEquals(documents.size(), successCount, results.toString());
        assertEquals(0, stockBalanceRepo.findByProductAndWarehouse(product, isolatedWarehouse)
                .orElseThrow()
                .getQuantity()
                .compareTo(BigDecimal.valueOf(documents.size())));
        assertEquals(documents.size(), movementCount);
        assertEquals(documents.size(), postedCount);
    }

    @Test
    public void productApprovals_postConcurrentlyWithoutExistingBalance_shouldKeepSingleBalanceRow() throws Exception {
        Warehouse isolatedWarehouse = createIsolatedWarehouse("MISS-ROW");

        List<Document> documents = List.of(
                createSubmittedProductApprove(isolatedWarehouse, BigDecimal.ONE),
                createSubmittedProductApprove(isolatedWarehouse, BigDecimal.ONE));

        List<PostAttemptResult> results = postConcurrently(documents);

        long successCount = results.stream().filter(PostAttemptResult::success).count();
        long movementCount = documents.stream().mapToLong(doc -> stockMovementRepo.countByDocumentId(doc.getId())).sum();
        long postedCount = documents.stream()
                .filter(doc -> documentRepository.findById(doc.getId()).orElseThrow().getDocumentStatus() == DocumentStatus.POSTED)
                .count();
        long balanceCount = stockBalanceRepo.countByProductAndWarehouse(product, isolatedWarehouse);

        assertEquals(documents.size(), successCount, results.toString());
        assertTrue(balanceCount <= 1, results.toString());
        assertEquals(documents.size(), movementCount);
        assertEquals(documents.size(), postedCount);
        assertEquals(1, balanceCount);

        if (balanceCount == 1) {
            assertEquals(0, stockBalanceRepo.findByProductAndWarehouse(product, isolatedWarehouse)
                    .orElseThrow()
                    .getQuantity()
                    .compareTo(BigDecimal.valueOf(documents.size())));
        }
    }

    @Test
    public void productApprovals_postConcurrentlyWithOppositeLineOrder_shouldKeepMultiRowStockConsistent() throws Exception {
        Warehouse isolatedWarehouse = createIsolatedWarehouse("MULTI-ROW");
        Product secondProduct = createSiblingProduct("P2");

        seedStockBalance(isolatedWarehouse, product, BigDecimal.ZERO);
        seedStockBalance(isolatedWarehouse, secondProduct, BigDecimal.ZERO);

        Document documentA = createSubmittedProductApprove(
                isolatedWarehouse,
                List.of(
                        line(product, BigDecimal.ONE, BigDecimal.ZERO),
                        line(secondProduct, BigDecimal.ONE, BigDecimal.ZERO)));
        Document documentB = createSubmittedProductApprove(
                isolatedWarehouse,
                List.of(
                        line(secondProduct, BigDecimal.ONE, BigDecimal.ZERO),
                        line(product, BigDecimal.ONE, BigDecimal.ZERO)));

        List<PostAttemptResult> results = postConcurrently(List.of(documentA, documentB));

        long successCount = results.stream().filter(PostAttemptResult::success).count();
        long movementCount = stockMovementRepo.countByDocumentId(documentA.getId()) + stockMovementRepo.countByDocumentId(documentB.getId());
        long postedCount = 0;
        postedCount += documentRepository.findById(documentA.getId()).orElseThrow().getDocumentStatus() == DocumentStatus.POSTED ? 1 : 0;
        postedCount += documentRepository.findById(documentB.getId()).orElseThrow().getDocumentStatus() == DocumentStatus.POSTED ? 1 : 0;

        assertEquals(2, successCount, results.toString());
        assertEquals(2, postedCount);
        assertEquals(4, movementCount);
        assertEquals(0, stockBalanceRepo.findByProductAndWarehouse(product, isolatedWarehouse)
                .orElseThrow()
                .getQuantity()
                .compareTo(BigDecimal.TWO));
        assertEquals(0, stockBalanceRepo.findByProductAndWarehouse(secondProduct, isolatedWarehouse)
                .orElseThrow()
                .getQuantity()
                .compareTo(BigDecimal.TWO));
    }

    private StockBalance seedStockBalance(BigDecimal quantity) {
        return seedStockBalance(warehouse, product, quantity);
    }

    private StockBalance seedStockBalance(Warehouse warehouse, BigDecimal quantity) {
        return seedStockBalance(warehouse, product, quantity);
    }

    private StockBalance seedStockBalance(Warehouse warehouse, Product stockProduct, BigDecimal quantity) {
        StockBalance stockBalance = stockBalanceRepo.findByProductAndWarehouse(stockProduct, warehouse).orElseGet(() -> {
            StockBalance sb = new StockBalance();
            sb.setProduct(stockProduct);
            sb.setWarehouse(warehouse);
            return sb;
        });
        stockBalance.setQuantity(quantity);
        stockBalance.setReservedQuantity(BigDecimal.ZERO);
        return stockBalanceRepo.saveAndFlush(stockBalance);
    }

    private StockBalance reloadStockBalance(Product stockProduct, Warehouse stockWarehouse) {
        entityManager.flush();
        entityManager.clear();
        return stockBalanceRepo.findByProductAndWarehouse(stockProduct, stockWarehouse).orElseThrow();
    }

    private Warehouse createIsolatedWarehouse(String prefix) {
        Warehouse isolatedWarehouse = new Warehouse();
        isolatedWarehouse.setCode(prefix + "-" + UUID.randomUUID());
        isolatedWarehouse.setName(prefix + " warehouse");
        return warehouseRepo.saveAndFlush(isolatedWarehouse);
    }

    private Product createSiblingProduct(String prefix) {
        Product sibling = new Product();
        sibling.setCode(prefix + "-" + UUID.randomUUID());
        sibling.setName(prefix + " product");
        sibling.setDescription(prefix + " product");
        sibling.setDefaultBarcode("BC-" + UUID.randomUUID());
        sibling.setBrand(product.getBrand());
        sibling.setProducer(product.getProducer());
        sibling.setProductType(product.getProductType());
        sibling.setProductGroup(product.getProductGroup());
        sibling.setProductCategory(product.getProductCategory());
        sibling.setProductClass(product.getProductClass());
        sibling.setCountry(product.getCountry());
        sibling.setDefaultUnit(product.getDefaultUnit());
        return productRepo.saveAndFlush(sibling);
    }

    private Document createSubmittedSalesOrder(Warehouse sourceWarehouse, BigDecimal quantity, BigDecimal discountRatio) {
        ProductLineCommand lineCommand = line(product, quantity, discountRatio);
        CreateDocumentCommand cmd = new CreateDocumentCommand(
                LocalDate.now(),
                salesOrderTypeId,
                "Concurrent sales order",
                partnerId,
                "CASH",
                priceListId,
                sourceWarehouse.getId(),
                null,
                List.of(lineCommand));
        Document document = documentActionService.createDraft(cmd);
        documentActionService.submit(document.getId());
        return document;
    }

    private Document createSubmittedProductApprove(Warehouse targetWarehouse, BigDecimal quantity) {
        return createSubmittedProductApprove(targetWarehouse, List.of(line(product, quantity, BigDecimal.ZERO)));
    }

    private Document createSubmittedProductApprove(Warehouse targetWarehouse, List<ProductLineCommand> lines) {
        CreateDocumentCommand cmd = new CreateDocumentCommand(
                LocalDate.now(),
                approveTypeId,
                "Concurrent product approve",
                null,
                null,
                priceListId,
                null,
                targetWarehouse.getId(),
                lines);
        Document document = documentActionService.createDraft(cmd);
        documentActionService.submit(document.getId());
        return document;
    }

    private ProductLineCommand line(Product stockProduct, BigDecimal quantity, BigDecimal discountRatio) {
        return new ProductLineCommand(
                stockProduct.getId(),
                quantity,
                BigDecimal.ONE,
                discountRatio);
    }

    private List<PostAttemptResult> postConcurrently(List<Document> documents) throws Exception {
        int threadCount = documents.size();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<PostAttemptResult>> futures = new ArrayList<>();

        try {
            for (Document document : documents) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    assertTrue(start.await(10, TimeUnit.SECONDS), "Start signal timed out");
                    try {
                        documentActionService.post(document.getId());
                        return PostAttemptResult.success(document.getId());
                    } catch (Exception e) {
                        Throwable rootCause = rootCause(e);
                        return PostAttemptResult.failure(document.getId(), rootCause);
                    }
                }));
            }

            assertTrue(ready.await(10, TimeUnit.SECONDS), "Worker threads did not become ready in time");
            start.countDown();

            List<PostAttemptResult> results = new ArrayList<>();
            for (Future<PostAttemptResult> future : futures) {
                results.add(future.get(20, TimeUnit.SECONDS));
            }
            return results;
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS), "Executor did not stop in time");
        }
    }

    private boolean isExpectedConcurrentSalesOrderFailure(PostAttemptResult result) {
        return "BusinessRuleException".equals(result.errorType())
                || result.errorType().contains("OptimisticLock")
                || isGeneratedValuesContentionFailure(result);
    }

    private boolean isGeneratedValuesContentionFailure(PostAttemptResult result) {
        return "HibernateException".equals(result.errorType())
                && result.errorMessage() != null
                && result.errorMessage().contains("The database returned no natively generated values");
    }

    private Throwable rootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private record PostAttemptResult(Long documentId, boolean success, String errorType, String errorMessage) {
        private static PostAttemptResult success(Long documentId) {
            return new PostAttemptResult(documentId, true, null, null);
        }

        private static PostAttemptResult failure(Long documentId, Throwable throwable) {
            return new PostAttemptResult(
                    documentId,
                    false,
                    throwable.getClass().getSimpleName(),
                    throwable.getMessage());
        }
    }
}

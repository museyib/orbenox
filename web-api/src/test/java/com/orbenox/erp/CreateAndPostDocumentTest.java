package com.orbenox.erp;

import com.orbenox.erp.domain.businesspartner.BusinessPartnerRepository;
import com.orbenox.erp.domain.price.PriceListRepository;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.stock.StockBalanceService;
import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.domain.warehouse.WarehouseRepository;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.consumer.EventConsumer;
import com.orbenox.erp.consumer.InboxEventRepository;
import com.orbenox.erp.messaging.command.StockMovementCommand.StockOperation;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.outbox.EventMessage;
import com.orbenox.erp.transaction.command.CreateProductApproveCommand;
import com.orbenox.erp.transaction.command.CreateSalesOrderCommand;
import com.orbenox.erp.transaction.command.ProductLineCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.JournalEntry;
import com.orbenox.erp.transaction.policy.approval.ApprovalPolicy;
import com.orbenox.erp.transaction.policy.approval.SalesOrderApprovalPolicy;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.repository.JournalEntryRepository;
import com.orbenox.erp.transaction.repository.JournalLineRepository;
import com.orbenox.erp.transaction.resolver.PolicyResolver;
import com.orbenox.erp.transaction.service.DocumentActionService;
import jakarta.persistence.EntityManager;
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
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class CreateAndPostDocumentTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    DocumentActionService<CreateSalesOrderCommand> salesOrderActionService;
    @Autowired
    DocumentActionService<CreateProductApproveCommand> productApproveActionService;
    @Autowired
    ProductRepository productRepo;
    @Autowired
    WarehouseRepository warehouseRepo;
    @Autowired
    PolicyResolver<ApprovalPolicy> approvalPolicyResolver;

    private Product product;
    private Warehouse warehouse;
    private Long priceListId;

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
    private StockBalanceService stockBalanceRepo;
    @Autowired
    private EventConsumer eventConsumer;
    @Autowired
    private InboxEventRepository inboxEventRepository;
    @Autowired
    private JsonMapper jsonMapper;
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
        partnerId = businessPartnerRepository.findAll().getFirst().getId();
        priceListId = priceListRepository.findAll().getFirst().getId();

        product = productRepo.findAll().getFirst();
        warehouse = warehouseRepo.findAll().getFirst();
    }

    @Test
    @Transactional
    public void productApprove_postShouldWaitForStockServiceEvent() {
        ProductLineCommand lineCommand = new ProductLineCommand(
                product.getId(),
                product.getDefaultUnit().getId(),
                BigDecimal.TEN,
                BigDecimal.ONE,
                BigDecimal.ZERO);
        CreateProductApproveCommand cmd = new CreateProductApproveCommand(
                LocalDate.now(),
                "Test",
                null,
                priceListId,
                warehouse.getId(),
                List.of(lineCommand));

        Document document = productApproveActionService.createDraft(cmd);

        productApproveActionService.submit(document.getId());
        productApproveActionService.post(document.getId());

        assertEquals(DocumentStatus.POSTING,
                documentRepository.findById(document.getId()).orElseThrow().getDocumentStatus());
    }

    @Test
    public void stockPostedEvent_shouldPostDocumentAndIgnoreDuplicateDelivery() {
        Warehouse isolatedWarehouse = createIsolatedWarehouse("EVENT-POST");
        Document document = createSubmittedProductApprove(isolatedWarehouse, BigDecimal.ONE);
        productApproveActionService.post(document.getId());

        assertEquals(DocumentStatus.POSTING,
                documentRepository.findById(document.getId()).orElseThrow().getDocumentStatus());

        long eventId = System.nanoTime();
        EventMessage event = new EventMessage(
                eventId,
                "PRODUCT_APPROVE_POSTED",
                "PRODUCT_APPROVE",
                document.getId().toString(),
                "1",
                jsonMapper.writeValueAsString(new StockUpdatedEvent(
                        true,
                        document.getId(),
                        "PRODUCT_APPROVE",
                        "Stock updated successfully",
                        List.of(new StockOperation(
                                document.getProductLines().getFirst().getId(),
                                product.getId(),
                                isolatedWarehouse.getId(),
                                BigDecimal.ONE,
                                1)))));

        eventConsumer.processEvent(event);

        assertEquals(DocumentStatus.POSTED,
                documentRepository.findById(document.getId()).orElseThrow().getDocumentStatus());
        assertEquals(1, inboxEventRepository.findAll().stream()
                .filter(inbox -> inbox.getEventId().equals(eventId))
                .count());

        Document updatedDocument = documentRepository.findById(document.getId()).orElseThrow();
        updatedDocument.setDocumentStatus(DocumentStatus.POSTING);
        documentRepository.saveAndFlush(updatedDocument);

        eventConsumer.processEvent(event);

        assertEquals(DocumentStatus.POSTING,
                documentRepository.findById(document.getId()).orElseThrow().getDocumentStatus());
        assertEquals(1, inboxEventRepository.findAll().stream()
                .filter(inbox -> inbox.getEventId().equals(eventId))
                .count());
    }

    @Test
    @Transactional
    public void salesOrder_postShouldCreateAccountingEntriesAndWaitForStockServiceEvent() {

        ProductLineCommand lineCommand = new ProductLineCommand(
                product.getId(),
                product.getDefaultUnit().getId(),
                BigDecimal.TEN,
                BigDecimal.ONE,
                BigDecimal.valueOf(50.0));
        CreateSalesOrderCommand cmd = new CreateSalesOrderCommand(
                LocalDate.now(),
                "Sales order",
                partnerId,
                "CASH",
                priceListId,
                warehouse.getId(),
                List.of(lineCommand));
        Document document = salesOrderActionService.createDraft(cmd);

        salesOrderActionService.submit(document.getId());
        approveSalesOrderIfRequired(document);
        salesOrderActionService.post(document.getId());

        assertEquals(DocumentStatus.POSTING,
                documentRepository.findById(document.getId()).orElseThrow().getDocumentStatus());

        JournalEntry journalEntry = journalEntryRepository.findByDocumentId(document.getId());
        assertNotNull(journalEntry);
        assertTrue(journalLineRepository.findByJournalEntryId(journalEntry.getId()).isEmpty());

        ApprovalPolicy policy = approvalPolicyResolver.resolve(document.getType());
        assertInstanceOf(SalesOrderApprovalPolicy.class, policy);
        assertTrue(document.getType().isApprovalRequired());
        assertTrue(policy.supports(document.getType()));
        assertTrue(policy.requiresApproval(document));
    }

    @Test
    public void salesOrder_postWithInsufficientStock_shouldWaitForStockFailureEvent() {

        ProductLineCommand lineCommand = new ProductLineCommand(
                product.getId(),
                product.getDefaultUnit().getId(),
                BigDecimal.TWO,
                BigDecimal.ONE,
                BigDecimal.ONE);
        CreateSalesOrderCommand cmd = new CreateSalesOrderCommand(
                LocalDate.now(),
                "Insufficient stock",
                partnerId,
                "CASH",
                priceListId,
                warehouse.getId(),
                List.of(lineCommand));
        Document document = salesOrderActionService.createDraft(cmd);

        salesOrderActionService.submit(document.getId());
        approveSalesOrderIfRequired(document);
        salesOrderActionService.post(document.getId());

        assertEquals(0, stockBalanceRepo.getStockBalanceItem(product.getId(), warehouse.getId()).getQuantity().compareTo(BigDecimal.ONE));
        assertNotNull(journalEntryRepository.findByDocumentId(document.getId()));
        assertEquals(DocumentStatus.POSTING,
                documentRepository.findById(document.getId()).orElseThrow().getDocumentStatus());
    }

    @Test
    public void salesOrders_postShouldRemainPostingUntilStockEventsArrive() {
        Warehouse isolatedWarehouse = createIsolatedWarehouse("CONC-SO");

        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            documents.add(createApprovedSalesOrder(isolatedWarehouse, BigDecimal.ONE, BigDecimal.ONE));
        }

        documents.forEach(document -> salesOrderActionService.post(document.getId()));

        assertTrue(documents.stream().allMatch(document ->
                documentRepository.findById(document.getId()).orElseThrow().getDocumentStatus() == DocumentStatus.POSTING));
        assertEquals(0, stockBalanceRepo.getStockBalanceItem(product.getId(), isolatedWarehouse.getId())
                .getQuantity().compareTo(BigDecimal.ONE));
    }

    @Test
    public void productApprovals_postShouldRemainPostingUntilStockEventsArrive() {
        Warehouse isolatedWarehouse = createIsolatedWarehouse("CONC-PA");

        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            documents.add(createSubmittedProductApprove(isolatedWarehouse, BigDecimal.ONE));
        }

        documents.forEach(document -> productApproveActionService.post(document.getId()));

        assertTrue(documents.stream().allMatch(document ->
                documentRepository.findById(document.getId()).orElseThrow().getDocumentStatus() == DocumentStatus.POSTING));
        assertEquals(0, stockBalanceRepo.getStockBalanceItem(product.getId(), isolatedWarehouse.getId())
                .getQuantity().compareTo(BigDecimal.ZERO));
    }

    @Test
    public void productApprovals_postShouldNotCreateStockBalanceBeforeStockServiceProcessesCommands() {
        Warehouse isolatedWarehouse = createIsolatedWarehouse("MISS-ROW");

        List<Document> documents = List.of(
                createSubmittedProductApprove(isolatedWarehouse, BigDecimal.ONE),
                createSubmittedProductApprove(isolatedWarehouse, BigDecimal.ONE));

        documents.forEach(document -> productApproveActionService.post(document.getId()));

        assertTrue(documents.stream().allMatch(document ->
                documentRepository.findById(document.getId()).orElseThrow().getDocumentStatus() == DocumentStatus.POSTING));
        assertEquals(0, stockBalanceRepo.getStockBalanceItem(product.getId(), isolatedWarehouse.getId()).getQuantity().compareTo(BigDecimal.ZERO));
    }

    @Test
    public void productApprovals_postShouldDeferMultiProductStockUpdatesToStockService() {
        Warehouse isolatedWarehouse = createIsolatedWarehouse("MULTI-ROW");
        Product secondProduct = createSiblingProduct("P2");

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

        productApproveActionService.post(documentA.getId());
        productApproveActionService.post(documentB.getId());

        assertEquals(DocumentStatus.POSTING,
                documentRepository.findById(documentA.getId()).orElseThrow().getDocumentStatus());
        assertEquals(DocumentStatus.POSTING,
                documentRepository.findById(documentB.getId()).orElseThrow().getDocumentStatus());
        assertEquals(0, stockBalanceRepo.getStockBalanceItem(product.getId(), isolatedWarehouse.getId())
                .getQuantity().compareTo(BigDecimal.ZERO));
        assertEquals(0, stockBalanceRepo.getStockBalanceItem(secondProduct.getId(), isolatedWarehouse.getId())
                .getQuantity().compareTo(BigDecimal.ZERO));
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

    private Document createApprovedSalesOrder(Warehouse sourceWarehouse, BigDecimal quantity, BigDecimal discountRatio) {
        ProductLineCommand lineCommand = line(product, quantity, discountRatio);
        CreateSalesOrderCommand cmd = new CreateSalesOrderCommand(
                LocalDate.now(),
                "Concurrent sales order",
                partnerId,
                "CASH",
                priceListId,
                sourceWarehouse.getId(),
                List.of(lineCommand));
        Document document = salesOrderActionService.createDraft(cmd);
        salesOrderActionService.submit(document.getId());
        approveSalesOrderIfRequired(document);
        return document;
    }

    private void approveSalesOrderIfRequired(Document document) {
        ApprovalPolicy policy = approvalPolicyResolver.resolve(document.getType());
        if (policy.requiresApproval(document)) {
            salesOrderActionService.approve(document.getId());
        }
    }

    private Document createSubmittedProductApprove(Warehouse targetWarehouse, BigDecimal quantity) {
        return createSubmittedProductApprove(targetWarehouse, List.of(line(product, quantity, BigDecimal.ZERO)));
    }

    private Document createSubmittedProductApprove(Warehouse targetWarehouse, List<ProductLineCommand> lines) {
        CreateProductApproveCommand cmd = new CreateProductApproveCommand(
                LocalDate.now(),
                "Concurrent product approve",
                null,
                priceListId,
                targetWarehouse.getId(),
                lines);
        Document document = productApproveActionService.createDraft(cmd);
        productApproveActionService.submit(document.getId());
        return document;
    }

    private ProductLineCommand line(Product stockProduct, BigDecimal quantity, BigDecimal discountRatio) {
        return new ProductLineCommand(
                stockProduct.getId(),
                stockProduct.getDefaultUnit().getId(),
                quantity,
                BigDecimal.ONE,
                discountRatio);
    }

}

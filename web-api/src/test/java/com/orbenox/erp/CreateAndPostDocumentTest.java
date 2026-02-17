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
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.command.ProductLineCommand;
import com.orbenox.erp.transaction.entity.*;
import com.orbenox.erp.transaction.policy.ApprovalPolicy;
import com.orbenox.erp.transaction.policy.SalesOrderApprovalPolicy;
import com.orbenox.erp.transaction.repository.*;
import com.orbenox.erp.transaction.resolver.ApprovalPolicyResolver;
import com.orbenox.erp.transaction.service.DocumentActionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.orbenox.erp.enums.JournalStatus.POSTED;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
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
    ApprovalPolicyResolver policyResolver;

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
    private StockBalanceRepository stockBalanceRepo;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void createEntities() {
        approveTypeId = transactionTypeRepo.findByCode("APPROVE").getId();
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
                "PA_001",
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
        StockBalance stockBalance = stockBalanceRepo.findByProductAndWarehouse(product, warehouse).orElseGet(() -> {
            StockBalance sb = new StockBalance();
            sb.setProduct(product);
            sb.setWarehouse(warehouse);
            sb.setQuantity(BigDecimal.TEN);
            return sb;
        });
        stockBalanceRepo.save(stockBalance);

        ProductLineCommand lineCommand = new ProductLineCommand(
                product.getId(),
                BigDecimal.TEN,
                BigDecimal.ONE,
                BigDecimal.valueOf(50.0));
        CreateDocumentCommand cmd = new CreateDocumentCommand(
                "S0-001",
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

        assertEquals(0, stockBalance.getQuantity().compareTo(BigDecimal.ZERO));
        ApprovalPolicy policy = policyResolver.resolve(document.getType());
        assertInstanceOf(SalesOrderApprovalPolicy.class, policy);
        assertTrue(document.getType().isApprovalRequired());
        assertTrue(policy.supports(document.getType()));
        assertTrue(policy.requiresApproval(document));
    }
}
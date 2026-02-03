package com.orbenox.erp;

import com.orbenox.erp.domain.businesspartner.BusinessPartnerRepository;
import com.orbenox.erp.domain.price.PriceListRepository;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.repository.ProductWarehouseRepository;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.domain.warehouse.WarehouseRepository;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.ProductLine;
import com.orbenox.erp.transaction.entity.StockContext;
import com.orbenox.erp.transaction.entity.StockMovement;
import com.orbenox.erp.transaction.repository.CommercialContextRepository;
import com.orbenox.erp.transaction.repository.ProductLineRepository;
import com.orbenox.erp.transaction.repository.StockContextRepository;
import com.orbenox.erp.transaction.repository.StockMovementRepository;
import com.orbenox.erp.transaction.service.DocumentActionService;
import com.orbenox.erp.transaction.service.DocumentService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Testcontainers
public class CreateAndPostDocumentTest {
    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    DocumentActionService documentActionService;
    @Autowired
    DocumentService documentService;
    @Autowired
    TransactionTypeRepository transactionTypeRepo;
    @Autowired
    ProductRepository productRepo;
    @Autowired
    WarehouseRepository warehouseRepo;
    @Autowired
    ProductWarehouseRepository productWarehouseRepo;
    @Autowired
    ProductLineRepository productLineRepo;
    @Autowired
    StockMovementRepository stockMovementRepo;
    @Autowired
    StockContextRepository stockContextRepo;
    @Autowired
    CommercialContextRepository commercialContextRepo;


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
    public void productApprove_createStockMovement() {
        CreateDocumentCommand cmd = new CreateDocumentCommand(
                "PA_001",
                LocalDate.now(),
                approveTypeId,
                "Test",
                null,
                null,
                priceListId
        );

        Document document = documentActionService.createDraft(cmd);
        ProductLine productLine = new ProductLine();
        productLine.setDocument(document);
        productLine.setProduct(product);
        productLine.setQuantity(BigDecimal.TEN);
        productLine.setUnitPrice(BigDecimal.ONE);
        productLine.setDiscount(BigDecimal.ZERO);
        productLineRepo.save(productLine);

        StockContext sc = new StockContext();
        sc.setDocument(document);
        sc.setTargetWarehouse(warehouse);
        stockContextRepo.save(sc);
        document.setStockContext(sc);
        document.setProductLines(List.of(productLine));

        documentActionService.submit(document.getId());
        documentActionService.post(document.getId());

        List<StockMovement> stockMovements = stockMovementRepo.findByDocumentId(document.getId());

        assertEquals(1,  stockMovements.size());
        assertEquals(BigDecimal.TEN, stockMovements.get(0).getQuantity());
    }

    @Test
    public void salesOrder_createsStockAndAccountingEntries() {
        CreateDocumentCommand cmd = new CreateDocumentCommand(
                "S0-001",
                LocalDate.now(),
                salesOrderTypeId,
                "Sales order",
                partnerId,
                "CASH",
                priceListId
        );
        Document document = documentActionService.createDraft(cmd);

        StockContext sc = new StockContext();
        sc.setDocument(document);
        sc.setTargetWarehouse(warehouse);
        stockContextRepo.save(sc);

        ProductLine productLine = new ProductLine();
        productLine.setDocument(document);
        productLine.setProduct(product);
        productLine.setQuantity(BigDecimal.TEN);
        productLine.setUnitPrice(BigDecimal.ONE);
        productLine.setDiscount(BigDecimal.valueOf(50));
        productLineRepo.save(productLine);

        document.setStockContext(sc);
        document.setProductLines(List.of(productLine));

        documentActionService.submit(document.getId());
        documentActionService.approve(document.getId());
        documentActionService.post(document.getId());
    }
}

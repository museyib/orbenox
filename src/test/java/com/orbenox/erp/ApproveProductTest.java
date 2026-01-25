package com.orbenox.erp;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.repository.ProductWarehouseRepository;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.domain.warehouse.WarehouseRepository;
import com.orbenox.erp.transaction.command.ApproveProductCommand;
import com.orbenox.erp.transaction.command.ProductLineCommand;
import com.orbenox.erp.transaction.entity.Document;
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

import static com.orbenox.erp.enums.DocumentStatus.POSTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Testcontainers
public class ApproveProductTest {
    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");
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
    private Long productId;
    private Long transactionTypeId;
    private Long warehouseId;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void createEntities() {
        TransactionType transactionType = transactionTypeRepo.findAll().get(0);
        transactionTypeId = transactionType.getId();

        Product product = productRepo.findAll().get(0);
        productId = product.getId();

        Warehouse warehouse = warehouseRepo.findAll().get(0);
        warehouseId = warehouse.getId();
    }

    @Test
    public void productApprovalIncreasesStock() {
        ApproveProductCommand cmd = new ApproveProductCommand(
                "PA_001",
                LocalDate.now(),
                transactionTypeId,
                "Test",
                warehouseId,
                List.of(new ProductLineCommand(productId, BigDecimal.TEN))
        );

        Document document = documentService.approveProduct(cmd);

        ProductWarehouse pw = productWarehouseRepo.findByProductIdAndWarehouseId(productId, warehouseId);
        assertEquals(POSTED, document.getDocumentStatus());
        assertEquals(BigDecimal.TEN, pw.getQuantity());
    }
}

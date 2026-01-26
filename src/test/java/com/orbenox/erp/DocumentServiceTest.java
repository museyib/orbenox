package com.orbenox.erp;

import com.orbenox.erp.domain.businesspartner.BusinessPartner;
import com.orbenox.erp.domain.businesspartner.BusinessPartnerRepository;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.enums.PartnerType;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.entity.CommercialContext;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.service.DocumentService;
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

import java.time.LocalDate;

import static com.orbenox.erp.enums.ApprovalStatus.AUTO_APPROVED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Testcontainers
public class DocumentServiceTest {
    private Long transactionTypeId;
    private Long businessPartnerId;

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentRepository documentRepo;

    @Autowired
    TransactionTypeRepository transactionTypeRepo;

    @Autowired
    BusinessPartnerRepository businessPartnerRepo;

    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void createEntities() {
        TransactionType transactionType = new TransactionType();
        transactionType.setCode("TEST");
        transactionType.setName("TEST");
        transactionTypeRepo.save(transactionType);
        transactionTypeId  = transactionType.getId();

        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setCode("TEST");
        businessPartner.setName("TEST");
        businessPartner.setType(PartnerType.PERSON);
        businessPartnerRepo.save(businessPartner);
        businessPartnerId  = businessPartner.getId();
    }

    @Test
    public void createDocument() {
        CreateDocumentCommand cmd = new CreateDocumentCommand(
                "DOC_001",
                LocalDate.now(),
                transactionTypeId,
                "Test Invoice",
                businessPartnerId,
                "CASH"
        );

        Document doc = documentService.createDocument(cmd);

        assertNotNull(doc.getId());
        assertEquals(DocumentStatus.DRAFT, doc.getDocumentStatus());
        assertEquals("DOC_001", doc.getNumber());
        assertEquals(AUTO_APPROVED, doc.getApprovalStatus());

        Document persisted = documentRepo.findById(doc.getId()).orElseThrow();
        CommercialContext cc = persisted.getCommercialContext();
        assertEquals(businessPartnerId, cc.getPartner().getId());
        assertEquals("CASH", cc.getPaymentMethod());
        assertNotNull(persisted.getCommercialContext());
    }
}

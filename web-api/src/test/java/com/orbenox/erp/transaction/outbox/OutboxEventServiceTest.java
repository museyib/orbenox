package com.orbenox.erp.transaction.outbox;

import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.outbox.OutboxEvent;
import com.orbenox.erp.outbox.OutboxEventRepository;
import com.orbenox.erp.outbox.OutboxEventService;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Outbox Event Service Transaction Tests")
public class OutboxEventServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OutboxEventService outboxEventService;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Test
    @DisplayName("Should create outbox event for a created document when transaction commits")
    void createOutboxEvent_whenDocumentCreatedAndTransactionCommits_shouldPersistEvent() {
        Document document = newDocument();

        transactionTemplate.execute(status -> {
            document.setType(transactionTypeRepository.findByCode("SALES_ORDER"));
            documentRepository.save(document);
            outboxEventService.createOutboxEvent(document, "CREATE", "SALES_ORDER");
            return null;
        });

        assertThat(document.getId()).isNotNull();
        OutboxEvent loaded = outboxEventRepository.findAll().stream()
                .filter(event -> document.getDocumentNo().equals(event.getAggregateId()))
                .findFirst()
                .orElseThrow();
        assertThat(loaded.getEventType()).isEqualTo("CREATE");
        assertThat(loaded.getAggregateType()).isEqualTo("SALES_ORDER");
        assertThat(loaded.getAggregateId()).isEqualTo(document.getDocumentNo());
        assertThat(loaded.getPayload()).contains(document.getDocumentNo());
        assertThat(loaded.getStatus()).isEqualTo("PENDING");
        assertThat(loaded.getCreatedAt()).isNotNull();
        assertThat(loaded.getPublishedAt()).isNull();
    }

    @Test
    @DisplayName("Should rollback document and outbox event when an exception occurs")
    void createOutboxEvent_whenTransactionFails_shouldRollbackDocumentAndEvent() {
        Document document = newDocument();
        assertThatThrownBy(() -> transactionTemplate.execute(status -> {
            document.setType(transactionTypeRepository.findByCode("SALES_ORDER"));
            documentRepository.save(document);
            outboxEventService.createOutboxEvent(document, "CREATE", "SALES_ORDER");
            throw new RuntimeException("Simulated transaction failure");
        })).isInstanceOf(RuntimeException.class)
           .hasMessage("Simulated transaction failure");

        assertThat(outboxEventRepository.findAll().stream()
                .noneMatch(event -> document.getDocumentNo().equals(event.getAggregateId()))).isTrue();
        if (document.getId() != null) {
            assertThat(documentRepository.findById(document.getId())).isEmpty();
        }
    }

    private Document newDocument() {
        Document document = new Document();
        document.setDocumentNo("SO-" + UUID.randomUUID());
        document.setDocumentDate(LocalDate.now());
        document.setDocumentStatus(DocumentStatus.DRAFT);
        return document;
    }
}

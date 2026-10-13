package com.orbenox.erp.consumer;

import com.orbenox.erp.message.EventMessage;
import com.orbenox.erp.messaging.command.StockMovementCommand.StockOperation;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.service.DocumentResolver;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class DocumentPostingTransactionIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private InboxEventRepository inboxEventRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JsonMapper jsonMapper;

    private TransactionTemplate transactionTemplate;

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        inboxEventRepository.deleteAll();
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    @Test
    void consumerFailureBeforeCommit_shouldRollbackInboxAndBusinessChangesTogether() {
        DocumentFixture fixture = createDocumentFixture();
        jdbcTemplate.execute("""
                CREATE FUNCTION fail_posted_quantity_update() RETURNS trigger AS $$
                BEGIN
                    RAISE EXCEPTION 'simulated consumer business update failure';
                END;
                $$ LANGUAGE plpgsql
                """);
        jdbcTemplate.execute("""
                CREATE TRIGGER fail_posted_quantity_update_trigger
                BEFORE UPDATE OF posted_quantity ON product_line
                FOR EACH ROW EXECUTE FUNCTION fail_posted_quantity_update()
                """);

        try {
            DocumentPostingService consumer = createConsumer(fixture.documentId());
            EventMessage message = stockUpdatedMessage(
                    9001L,
                    fixture.documentId(),
                    fixture.productLineId(),
                    new BigDecimal("2"));

            assertThatThrownBy(() -> transactionTemplate.executeWithoutResult(
                    status -> consumer.handleStockUpdatedEvent(message)))
                    .isInstanceOf(RuntimeException.class);

            entityManager.clear();
            assertThat(jdbcTemplate.queryForObject(
                    "SELECT document_status FROM document WHERE id = ?",
                    String.class,
                    fixture.documentId())).isEqualTo("IN_PROGRESS");
            assertThat(jdbcTemplate.queryForObject(
                    "SELECT posted_quantity FROM product_line WHERE id = ?",
                    BigDecimal.class,
                    fixture.productLineId())).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(inboxEventCount(message.eventId())).isZero();
        } finally {
            jdbcTemplate.execute("DROP TRIGGER IF EXISTS fail_posted_quantity_update_trigger ON product_line");
            jdbcTemplate.execute("DROP FUNCTION IF EXISTS fail_posted_quantity_update()");
            deleteDocumentFixture(fixture);
        }
    }

    @Test
    void replayAfterOriginalProcessing_shouldNotRepeatBusinessEffect() {
        DocumentFixture fixture = createDocumentFixture();
        try {
            DocumentPostingService consumer = createConsumer(fixture.documentId());
            EventMessage message = stockUpdatedMessage(
                    9002L,
                    fixture.documentId(),
                    fixture.productLineId(),
                    new BigDecimal("2"));

            transactionTemplate.executeWithoutResult(status -> consumer.handleStockUpdatedEvent(message));
            entityManager.clear();
            transactionTemplate.executeWithoutResult(status -> consumer.handleStockUpdatedEvent(message));
            entityManager.clear();

            assertThat(jdbcTemplate.queryForObject(
                    "SELECT posted_quantity FROM product_line WHERE id = ?",
                    BigDecimal.class,
                    fixture.productLineId())).isEqualByComparingTo(new BigDecimal("2"));
            assertThat(inboxEventCount(message.eventId())).isEqualTo(1);
        } finally {
            deleteDocumentFixture(fixture);
        }
    }

    private DocumentPostingService createConsumer(long documentId) {
        DocumentResolver documentResolver = mock(DocumentResolver.class);
        when(documentResolver.resolveOptional(documentId, "SALES_ORDER"))
                .thenAnswer(invocation -> Optional.of(entityManager.find(Document.class, documentId)));
        return new DocumentPostingService(
                documentResolver,
                inboxEventRepository,
                jsonMapper,
                new SimpleMeterRegistry());
    }

    private EventMessage stockUpdatedMessage(
            long eventId,
            long documentId,
            long productLineId,
            BigDecimal quantity) {
        StockUpdatedEvent payload = new StockUpdatedEvent(
                true,
                documentId,
                "SALES_ORDER",
                "stock updated",
                List.of(new StockOperation(productLineId, 1L, 1L, quantity, 1)));
        return new EventMessage(
                eventId,
                "STOCK_POSTED",
                "DOCUMENT",
                Long.toString(documentId),
                "1",
                jsonMapper.writeValueAsString(payload),
                null);
    }

    private DocumentFixture createDocumentFixture() {
        long documentId = jdbcTemplate.queryForObject("""
                INSERT INTO document(document_no, document_date, type_id, document_status, created_by)
                VALUES (?, CURRENT_DATE,
                        (SELECT id FROM transaction_type WHERE code = 'SALES_ORDER'),
                        'IN_PROGRESS', 'test')
                RETURNING id
                """, Long.class, "CONSUMER-TEST-" + UUID.randomUUID());
        long productLineId = jdbcTemplate.queryForObject("""
                INSERT INTO product_line(
                    document_id, product_id, unit_id, quantity, posted_quantity, unit_price, discount)
                VALUES (?, (SELECT id FROM product WHERE code = 'P001'),
                        (SELECT id FROM unit WHERE code = 'PCS'), 10, 0, 1, 0)
                RETURNING id
                """, Long.class, documentId);
        return new DocumentFixture(documentId, productLineId);
    }

    private int inboxEventCount(long eventId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM inbox_event WHERE consumer_name = 'web-api' AND event_id = ?",
                Integer.class,
                eventId);
    }

    private void deleteDocumentFixture(DocumentFixture fixture) {
        jdbcTemplate.update("DELETE FROM product_line WHERE id = ?", fixture.productLineId());
        jdbcTemplate.update("DELETE FROM document WHERE id = ?", fixture.documentId());
    }

    private record DocumentFixture(long documentId, long productLineId) {
    }
}

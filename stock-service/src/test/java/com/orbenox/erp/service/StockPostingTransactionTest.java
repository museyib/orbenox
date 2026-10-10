package com.orbenox.erp.service;

import com.orbenox.erp.consumer.EventConsumer;
import com.orbenox.erp.message.EventMessage;
import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.messaging.command.StockMovementCommand.StockOperation;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.outbox.OutboxEvent;
import com.orbenox.erp.outbox.OutboxEventRepository;
import com.orbenox.erp.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
@Testcontainers
class StockPostingTransactionTest {

    private static final AtomicLong DOCUMENT_IDS = new AtomicLong(100L);

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    @Autowired
    private EventConsumer eventConsumer;
    @Autowired
    private StockMovementRepository stockMovementRepository;
    @Autowired
    private OutboxEventRepository outboxEventRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JsonMapper jsonMapper;

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void makeGeneratedBalanceColumnNullable() {
        jdbcTemplate.execute("ALTER TABLE stock_balance ALTER COLUMN free_quantity DROP NOT NULL");
        jdbcTemplate.execute("ALTER TABLE stock_balance ALTER COLUMN reserved_quantity SET DEFAULT 0");
        jdbcTemplate.execute("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_stock_balance_product_warehouse_test
                ON stock_balance(product_id, warehouse_id)
                """);
    }

    @Test
    void processEvent_whenSalesOrderHasInsufficientStock_shouldRollbackStockAndPublishFailure() {
        Long documentId = DOCUMENT_IDS.incrementAndGet();
        saveStockBalance(56L, BigDecimal.ONE);

        EventMessage eventMessage = new EventMessage(
                1L,
                "STOCK_POSTED",
                "PRODUCT_APPROVE",
                documentId.toString(),
                "",
                new JsonMapper().writeValueAsString(command(documentId, "SALES_ORDER"))
        );

        eventConsumer.processEvent(eventMessage);

        BigDecimal savedQuantity = jdbcTemplate.queryForObject(
                "SELECT quantity FROM stock_balance WHERE product_id = 34 AND warehouse_id = 56",
                BigDecimal.class);
        assertThat(savedQuantity).isEqualByComparingTo("1");
        assertThat(stockMovementRepository.countByDocumentId(documentId)).isZero();

        List<OutboxEvent> events = eventsFor(documentId);
        assertThat(events).hasSize(1);
        OutboxEvent outboxEvent = events.getFirst();
        StockUpdatedEvent failureEvent = jsonMapper.readValue(outboxEvent.getPayload(), StockUpdatedEvent.class);
        assertThat(failureEvent.success()).isFalse();
        assertThat(failureEvent.documentId()).isEqualTo(documentId);
        assertThat(failureEvent.operations()).containsExactly(
                new StockOperation(1L, 34L, 56L, BigDecimal.TWO, -1));
        assertThat(outboxEvent.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void processEvent_whenProductApproveSucceeds_shouldPersistStockAndSuccessEvent() {
        Long documentId = DOCUMENT_IDS.incrementAndGet();

        EventMessage eventMessage = new EventMessage(
                1L,
                "STOCK_POSTED",
                "PRODUCT_APPROVE",
                documentId.toString(),
                "",
                new JsonMapper().writeValueAsString(command(documentId, "PRODUCT_APPROVE"))
        );
        eventConsumer.processEvent(eventMessage);

        BigDecimal savedQuantity = jdbcTemplate.queryForObject(
                "SELECT quantity FROM stock_balance WHERE product_id = 34 AND warehouse_id = 57",
                BigDecimal.class);
        assertThat(savedQuantity).isEqualByComparingTo("5");
        assertThat(stockMovementRepository.findByDocumentId(documentId))
                .singleElement()
                .satisfies(movement -> {
                    assertThat(movement.getProductId()).isEqualTo(34L);
                    assertThat(movement.getWarehouseId()).isEqualTo(57L);
                    assertThat(movement.getQuantity()).isEqualByComparingTo("5");
                });

        OutboxEvent event = eventsFor(documentId).getFirst();
        StockUpdatedEvent successEvent = jsonMapper.readValue(event.getPayload(), StockUpdatedEvent.class);
        assertThat(successEvent.success()).isTrue();
        assertThat(successEvent.documentId()).isEqualTo(documentId);
        assertThat(successEvent.operations()).containsExactly(
                new StockOperation(1L, 34L, 57L, BigDecimal.valueOf(5), 1));
        assertThat(event.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void processEvent_whenSalesOrderSucceeds_shouldCommitStockMovementAndSuccessOutbox() {
        Long documentId = DOCUMENT_IDS.incrementAndGet();
        saveStockBalance(56L, BigDecimal.valueOf(5));

        EventMessage eventMessage = new EventMessage(
                1L,
                "STOCK_POSTED",
                "PRODUCT_APPROVE",
                documentId.toString(),
                "",
                new JsonMapper().writeValueAsString(command(documentId, "SALES_ORDER"))
        );

        eventConsumer.processEvent(eventMessage);

        assertThat(stockBalanceQuantity(56L)).isEqualByComparingTo("3");
        assertThat(stockMovementRepository.countByDocumentId(documentId)).isEqualTo(1);
        assertThat(stockMovementRepository.findByDocumentId(documentId))
                .singleElement()
                .satisfies(movement -> {
                    assertThat(movement.getProductId()).isEqualTo(34L);
                    assertThat(movement.getWarehouseId()).isEqualTo(56L);
                    assertThat(movement.getQuantity()).isEqualByComparingTo("-2");
                });

        OutboxEvent event = eventsFor(documentId).getFirst();
        StockUpdatedEvent successEvent = jsonMapper.readValue(event.getPayload(), StockUpdatedEvent.class);
        assertThat(successEvent.success()).isTrue();
        assertThat(successEvent.documentId()).isEqualTo(documentId);
        assertThat(successEvent.operations()).containsExactly(
                new StockOperation(1L, 34L, 56L, BigDecimal.TWO, -1));
        assertThat(event.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void processEvent_whenOutboxInsertHasTechnicalFailure_shouldRollbackAndPropagateForRabbitRetry() {
        Long documentId = DOCUMENT_IDS.incrementAndGet();
        saveStockBalance(56L, BigDecimal.valueOf(5));
        jdbcTemplate.execute("""
                CREATE OR REPLACE FUNCTION fail_sales_order_outbox_insert()
                RETURNS trigger AS $$
                BEGIN
                    RAISE EXCEPTION 'simulated technical outbox failure';
                END;
                $$ LANGUAGE plpgsql
                """);
        jdbcTemplate.execute("""
                CREATE TRIGGER fail_sales_order_outbox_insert_trigger
                BEFORE INSERT ON outbox_event
                FOR EACH ROW
                WHEN (NEW.event_type = 'SALES_ORDER_POSTED')
                EXECUTE FUNCTION fail_sales_order_outbox_insert()
                """);


        EventMessage eventMessage = new EventMessage(
                1L,
                "STOCK_POSTED",
                "PRODUCT_APPROVE",
                documentId.toString(),
                "",
                new JsonMapper().writeValueAsString(command(documentId, "SALES_ORDER"))
        );
        try {
            assertThatThrownBy(() -> eventConsumer.processEvent(eventMessage))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("simulated technical outbox failure");
        } finally {
            jdbcTemplate.execute("DROP TRIGGER IF EXISTS fail_sales_order_outbox_insert_trigger ON outbox_event");
            jdbcTemplate.execute("DROP FUNCTION IF EXISTS fail_sales_order_outbox_insert()");
        }

        assertThat(stockBalanceQuantity(56L)).isEqualByComparingTo("5");
        assertThat(stockMovementRepository.countByDocumentId(documentId)).isZero();
        assertThat(eventsFor(documentId)).isEmpty();
    }

    private StockMovementCommand command(Long documentId, String typeCode) {
        StockOperation operation = "SALES_ORDER".equals(typeCode)
                ? new StockOperation(1L, 34L, 56L, BigDecimal.TWO, -1)
                : new StockOperation(1L, 34L, 57L, BigDecimal.valueOf(5), 1);
        return new StockMovementCommand(
                documentId,
                typeCode,
                new ArrayList<>(List.of(operation)));
    }

    private void saveStockBalance(Long warehouseId, BigDecimal quantity) {
        jdbcTemplate.update("""
                INSERT INTO stock_balance(product_id, warehouse_id, quantity, reserved_quantity)
                VALUES (34, ?, ?, 0)
                ON CONFLICT (product_id, warehouse_id)
                DO UPDATE SET quantity = EXCLUDED.quantity, reserved_quantity = EXCLUDED.reserved_quantity
                """,
                warehouseId,
                quantity);
    }

    private BigDecimal stockBalanceQuantity(Long warehouseId) {
        return jdbcTemplate.queryForObject(
                "SELECT quantity FROM stock_balance WHERE product_id = 34 AND warehouse_id = ?",
                BigDecimal.class,
                warehouseId);
    }

    private List<OutboxEvent> eventsFor(Long documentId) {
        return outboxEventRepository.findAll().stream()
                .filter(event -> documentId.toString().equals(event.getAggregateId()))
                .toList();
    }
}

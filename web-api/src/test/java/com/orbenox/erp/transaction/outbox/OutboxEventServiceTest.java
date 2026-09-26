package com.orbenox.erp.transaction.outbox;

import com.orbenox.erp.domain.currency.Currency;
import com.orbenox.erp.domain.currency.CurrencyRepository;
import com.orbenox.erp.idempotency.IdempotencyExecutor;
import com.orbenox.erp.outbox.OutboxEvent;
import com.orbenox.erp.outbox.OutboxEventRepository;
import com.orbenox.erp.outbox.OutboxEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
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
    private CurrencyRepository currencyRepository;

    @Autowired
    private IdempotencyExecutor idempotencyExecutor;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Test
    @DisplayName("Should persist outbox event with correct attributes and timestamps when transaction commits")
    void save_whenTransactionCommits_shouldPersistOutboxEventWithAllFields() {
        String aggregateId = UUID.randomUUID().toString();
        OutboxEvent event = new OutboxEvent();
        event.setEventType("CREATE");
        event.setAggregateType("SALES_ORDER");
        event.setAggregateId(aggregateId);
        event.setAggregateVersion("1");
        event.setPayload("{\"documentNo\":\"SO-001\",\"total\":150.00}");
        event.setStatus("PENDING");

        transactionTemplate.execute(status -> {
            outboxEventService.save(event);
            return null;
        });

        assertThat(event.getId()).isNotNull();
        assertThat(event.getCreatedAt()).isNotNull();

        Optional<OutboxEvent> saved = outboxEventRepository.findById(event.getId());
        assertThat(saved).isPresent();
        OutboxEvent loaded = saved.get();
        assertThat(loaded.getEventType()).isEqualTo("CREATE");
        assertThat(loaded.getAggregateType()).isEqualTo("SALES_ORDER");
        assertThat(loaded.getAggregateId()).isEqualTo(aggregateId);
        assertThat(loaded.getAggregateVersion()).isEqualTo("1");
        assertThat(loaded.getPayload()).isEqualTo("{\"documentNo\":\"SO-001\",\"total\":150.00}");
        assertThat(loaded.getStatus()).isEqualTo("PENDING");
        assertThat(loaded.getCreatedAt()).isNotNull();
        assertThat(loaded.getPublishedAt()).isNull();
    }

    @Test
    @DisplayName("Should rollback outbox event when an exception occurs in the transaction")
    void save_whenExceptionOccursInTransaction_shouldRollbackOutboxEvent() {
        String aggregateId = UUID.randomUUID().toString();
        OutboxEvent event = new OutboxEvent();
        event.setEventType("CREATE");
        event.setAggregateType("SALES_ORDER");
        event.setAggregateId(aggregateId);
        event.setPayload("{\"documentNo\":\"SO-002\"}");
        event.setStatus("PENDING");

        assertThatThrownBy(() -> transactionTemplate.execute(status -> {
            outboxEventService.save(event);
            throw new RuntimeException("Simulated transaction failure");
        })).isInstanceOf(RuntimeException.class)
           .hasMessage("Simulated transaction failure");

        if (event.getId() != null) {
            assertThat(outboxEventRepository.findById(event.getId())).isEmpty();
        }
        assertThat(outboxEventRepository.findAll().stream()
                .anyMatch(e -> aggregateId.equals(e.getAggregateId()))).isFalse();
    }

    @Test
    @DisplayName("Should rollback both business entity and outbox event atomically when exception occurs")
    void save_whenCombinedWithOtherEntitiesInTransaction_shouldRollbackAllEntitiesAtomically() {
        String testCurrencyCode = "OXT_" + UUID.randomUUID().toString().substring(0, 4);
        String aggregateId = UUID.randomUUID().toString();

        Currency currency = new Currency();
        currency.setCode(testCurrencyCode);
        currency.setName("Outbox Test Currency");
        currency.setEnabled(true);
        currency.setDeleted(false);

        OutboxEvent event = new OutboxEvent();
        event.setEventType("CREATE");
        event.setAggregateType("CURRENCY");
        event.setAggregateId(aggregateId);
        event.setPayload("{\"code\":\"" + testCurrencyCode + "\"}");
        event.setStatus("PENDING");

        assertThatThrownBy(() -> transactionTemplate.execute(status -> {
            currencyRepository.save(currency);
            outboxEventService.save(event);
            throw new IllegalStateException("Simulated business error during currency creation");
        })).isInstanceOf(IllegalStateException.class)
           .hasMessage("Simulated business error during currency creation");

        if (currency.getId() != null) {
            assertThat(currencyRepository.findById(currency.getId())).isEmpty();
        }
        assertThat(currencyRepository.findAll().stream()
                .anyMatch(c -> testCurrencyCode.equals(c.getCode()))).isFalse();

        if (event.getId() != null) {
            assertThat(outboxEventRepository.findById(event.getId())).isEmpty();
        }
        assertThat(outboxEventRepository.findAll().stream()
                .anyMatch(e -> aggregateId.equals(e.getAggregateId()))).isFalse();
    }

    @Test
    @DisplayName("Should persist outbox event when IdempotencyExecutor operation succeeds")
    void idempotencyExecutor_whenOperationSucceeds_shouldPersistOutboxEvent() {
        String key = "outbox-idem-" + UUID.randomUUID();
        String hash = "hash-" + UUID.randomUUID();
        String aggregateId = UUID.randomUUID().toString();

        OutboxEvent event = new OutboxEvent();
        event.setEventType("CREATE");
        event.setAggregateType("SALES_ORDER");
        event.setAggregateId(aggregateId);
        event.setPayload("{\"documentNo\":\"SO-IDEM-001\"}");
        event.setStatus("PENDING");

        Object response = idempotencyExecutor.execute(key, hash, event, () ->
                ResponseEntity.ok("{\"success\":true}")
        );

        assertThat(response).isNotNull();
        assertThat(event.getId()).isNotNull();

        Optional<OutboxEvent> saved = outboxEventRepository.findById(event.getId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getAggregateId()).isEqualTo(aggregateId);
        assertThat(saved.get().getStatus()).isEqualTo("PENDING");
        assertThat(saved.get().getEventType()).isEqualTo("CREATE");
        assertThat(saved.get().getAggregateType()).isEqualTo("SALES_ORDER");
    }

    @Test
    @DisplayName("Should rollback outbox event when IdempotencyExecutor operation throws exception")
    void idempotencyExecutor_whenOperationThrowsException_shouldRollbackOutboxEvent() {
        String key = "outbox-fail-" + UUID.randomUUID();
        String hash = "hash-fail-" + UUID.randomUUID();
        String aggregateId = UUID.randomUUID().toString();

        OutboxEvent event = new OutboxEvent();
        event.setEventType("CREATE");
        event.setAggregateType("SALES_ORDER");
        event.setAggregateId(aggregateId);
        event.setPayload("{\"documentNo\":\"SO-FAIL-001\"}");
        event.setStatus("PENDING");

        assertThatThrownBy(() -> idempotencyExecutor.execute(key, hash, event, () -> {
            throw new RuntimeException("Business operation failed in idempotency executor");
        })).isInstanceOf(RuntimeException.class)
           .hasMessage("Business operation failed in idempotency executor");

        if (event.getId() != null) {
            assertThat(outboxEventRepository.findById(event.getId())).isEmpty();
        }
        assertThat(outboxEventRepository.findAll().stream()
                .anyMatch(e -> aggregateId.equals(e.getAggregateId()))).isFalse();
    }
}

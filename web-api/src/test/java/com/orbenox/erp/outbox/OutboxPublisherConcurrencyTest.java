package com.orbenox.erp.outbox;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.DOCUMENT_CREATED_KEY;
import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.ERP_EXCHANGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class OutboxPublisherConcurrencyTest {

    private static final int REPLICA_COUNT = 3;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void publishEvents_whenReplicasCompeteForSamePendingRow_shouldPublishItOnce() throws Exception {
        outboxEventRepository.deleteAll();
        OutboxEvent pendingEvent = outboxEventRepository.saveAndFlush(pendingEvent());
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        CountDownLatch allReplicasReady = new CountDownLatch(REPLICA_COUNT);
        CountDownLatch startReplicas = new CountDownLatch(1);
        CountDownLatch firstPublishStarted = new CountDownLatch(1);
        CountDownLatch releaseFirstPublish = new CountDownLatch(1);
        CountDownLatch otherReplicasFinished = new CountDownLatch(REPLICA_COUNT - 1);
        AtomicInteger sendCount = new AtomicInteger();

        doAnswer(invocation -> {
            if (sendCount.incrementAndGet() == 1) {
                firstPublishStarted.countDown();
                if (!releaseFirstPublish.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Timed out waiting to release the first publish");
                }
            }
            return null;
        }).when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(EventMessage.class));

        OutboxPublisher publisher = new OutboxPublisher(outboxEventRepository, rabbitTemplate);
        ExecutorService executor = Executors.newFixedThreadPool(REPLICA_COUNT);
        try {
            List<? extends Future<?>> publishAttempts = java.util.stream.IntStream.range(0, REPLICA_COUNT)
                    .mapToObj(ignored -> executor.submit(() -> {
                        allReplicasReady.countDown();
                        if (!startReplicas.await(10, TimeUnit.SECONDS)) {
                            throw new IllegalStateException("Timed out waiting to start publisher replicas");
                        }
                        try {
                            TransactionTemplate transaction = new TransactionTemplate(transactionManager);
                            transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
                            transaction.executeWithoutResult(status -> publisher.publishEvents());
                        } finally {
                            otherReplicasFinished.countDown();
                        }
                        return null;
                    }))
                    .toList();

            assertThat(allReplicasReady.await(10, TimeUnit.SECONDS)).isTrue();
            startReplicas.countDown();
            assertThat(firstPublishStarted.await(10, TimeUnit.SECONDS)).isTrue();
            assertThat(otherReplicasFinished.await(5, TimeUnit.SECONDS))
                    .as("the other replicas should finish while the first holds the row lock")
                    .isTrue();
            assertThat(sendCount.get())
                    .as("only the replica holding the pending row lock should send")
                    .isEqualTo(1);

            releaseFirstPublish.countDown();
            for (Future<?> publishAttempt : publishAttempts) {
                publishAttempt.get(10, TimeUnit.SECONDS);
            }
        } finally {
            releaseFirstPublish.countDown();
            executor.shutdownNow();
        }

        EventMessage expectedMessage = new EventMessage(
                pendingEvent.getId(),
                "SALES_ORDER_CREATED",
                "SALES_ORDER",
                "SO-123",
                "1",
                "{\"documentId\":\"SO-123\"}");
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq(ERP_EXCHANGE), eq(DOCUMENT_CREATED_KEY), eq(expectedMessage));
        assertThat(sendCount).hasValue(1);
        OutboxEvent publishedEvent = outboxEventRepository.findById(pendingEvent.getId()).orElseThrow();
        assertThat(publishedEvent.getStatus()).isEqualTo("PUBLISHED");
        assertThat(publishedEvent.getPublishedAt()).isNotNull();
    }

    private OutboxEvent pendingEvent() {
        OutboxEvent event = new OutboxEvent();
        event.setCreatedAt(LocalDateTime.now());
        event.setEventType("SALES_ORDER_CREATED");
        event.setAggregateType("SALES_ORDER");
        event.setAggregateId("SO-123");
        event.setAggregateVersion("1");
        event.setPayload("{\"documentId\":\"SO-123\"}");
        event.setRoutingKey(DOCUMENT_CREATED_KEY);
        event.setStatus("PENDING");
        return event;
    }
}

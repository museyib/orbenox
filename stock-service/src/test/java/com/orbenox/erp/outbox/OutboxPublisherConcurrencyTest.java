package com.orbenox.erp.outbox;

import com.orbenox.erp.message.EventMessage;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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
@Import({OutboxEventService.class, OutboxPublisherConcurrencyTest.TestBeans.class})
class OutboxPublisherConcurrencyTest {

    private static final int REPLICA_COUNT = 3;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("publisherOne")
    private OutboxPublisher publisher;

    @Autowired
    @Qualifier("publisherTwo")
    private OutboxPublisher competingPublisher;

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
            CorrelationData correlationData = invocation.getArgument(3);
            correlationData.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbitTemplate).convertAndSend(
                any(String.class),
                any(String.class),
                any(EventMessage.class),
                any(CorrelationData.class));

        ExecutorService executor = Executors.newFixedThreadPool(REPLICA_COUNT);
        try {
            List<? extends Future<?>> publishAttempts = java.util.stream.IntStream.range(0, REPLICA_COUNT)
                    .mapToObj(ignored -> executor.submit(() -> {
                        allReplicasReady.countDown();
                        if (!startReplicas.await(10, TimeUnit.SECONDS)) {
                            throw new IllegalStateException("Timed out waiting to start publisher replicas");
                        }
                        try {
                            publisher.publishEvents();
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
                "{\"documentId\":\"SO-123\"}",
                "correlation-outbox-test");
        verify(rabbitTemplate, times(1))
                .convertAndSend(
                        eq(ERP_EXCHANGE),
                        eq(DOCUMENT_CREATED_KEY),
                        eq(expectedMessage),
                        any(CorrelationData.class));
        assertThat(sendCount).hasValue(1);
        OutboxEvent publishedEvent = outboxEventRepository.findById(pendingEvent.getId()).orElseThrow();
        assertThat(publishedEvent.getStatus()).isEqualTo("PUBLISHED");
        assertThat(publishedEvent.getPublishedAt()).isNotNull();
    }

    @Test
    void publishEvents_whenCompetingPublisherStartsDuringSlowBatch_shouldPublishEachEventOnce() throws Exception {
        outboxEventRepository.deleteAll();
        List<OutboxEvent> pendingEvents = IntStream.range(0, 5)
                .mapToObj(index -> {
                    OutboxEvent event = pendingEvent();
                    event.setAggregateId("SO-" + index);
                    return event;
                })
                .toList();
        pendingEvents = outboxEventRepository.saveAllAndFlush(pendingEvents);
        List<Long> expectedEventIds = pendingEvents.stream().map(OutboxEvent::getId).toList();

        CountDownLatch firstPublishStarted = new CountDownLatch(1);
        CountDownLatch releaseFirstPublish = new CountDownLatch(1);
        AtomicInteger sendCount = new AtomicInteger();
        ConcurrentHashMap<Long, AtomicInteger> sendsByEventId = new ConcurrentHashMap<>();

        doAnswer(invocation -> {
            EventMessage eventMessage = invocation.getArgument(2);
            sendsByEventId.computeIfAbsent(eventMessage.eventId(), ignored -> new AtomicInteger()).incrementAndGet();
            if (sendCount.incrementAndGet() == 1) {
                firstPublishStarted.countDown();
                if (!releaseFirstPublish.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Timed out waiting to release the first publish");
                }
            }
            CorrelationData correlationData = invocation.getArgument(3);
            correlationData.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbitTemplate).convertAndSend(
                any(String.class),
                any(String.class),
                any(EventMessage.class),
                any(CorrelationData.class));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<?> firstPublisherAttempt = executor.submit(publisher::publishEvents);
            assertThat(firstPublishStarted.await(10, TimeUnit.SECONDS))
                    .as("the first publisher should reach RabbitMQ and hold its batch transaction")
                    .isTrue();

            Future<?> competingPublisherAttempt = executor.submit(competingPublisher::publishEvents);
            competingPublisherAttempt.get(5, TimeUnit.SECONDS);
            assertThat(sendCount.get())
                    .as("the competing publisher must not send any event from the locked batch")
                    .isEqualTo(1);

            releaseFirstPublish.countDown();
            firstPublisherAttempt.get(10, TimeUnit.SECONDS);
        } finally {
            releaseFirstPublish.countDown();
            executor.shutdownNow();
        }

        assertThat(sendCount).hasValue(5);
        assertThat(sendsByEventId.keySet()).containsExactlyInAnyOrderElementsOf(expectedEventIds);
        assertThat(sendsByEventId.values()).allSatisfy(count -> assertThat(count).hasValue(1));
        assertThat(outboxEventRepository.findAllById(expectedEventIds))
                .allSatisfy(event -> assertThat(event.getStatus()).isEqualTo("PUBLISHED"));
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
        event.setCorrelationId("correlation-outbox-test");
        return event;
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        RabbitTemplate rabbitTemplate() {
            return mock(RabbitTemplate.class);
        }

        @Bean
        OutboxPublisher publisherOne(OutboxEventService outboxEventService, RabbitTemplate rabbitTemplate) {
            return new OutboxPublisher(outboxEventService, rabbitTemplate, new SimpleMeterRegistry());
        }

        @Bean
        OutboxPublisher publisherTwo(OutboxEventService outboxEventService, RabbitTemplate rabbitTemplate) {
            return new OutboxPublisher(outboxEventService, rabbitTemplate, new SimpleMeterRegistry());
        }
    }
}

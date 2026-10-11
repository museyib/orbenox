package com.orbenox.erp.consumer;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.orbenox.erp.config.CorrelationIdFilter;
import com.orbenox.erp.config.JpaAuditingConfig;
import com.orbenox.erp.message.EventMessage;
import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.outbox.OutboxEvent;
import com.orbenox.erp.outbox.OutboxEventRepository;
import com.orbenox.erp.outbox.OutboxEventService;
import com.orbenox.erp.outbox.OutboxPublisher;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.ERP_EXCHANGE;
import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.DOCUMENT_POSTED_KEY;
import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.STOCK_POSTED_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CorrelationIdFlowIntegrationTest {

    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Container
    static final GenericContainer<?> rabbit = new GenericContainer<>(DockerImageName.parse("rabbitmq:3.13-alpine"))
            .withEnv("RABBITMQ_DEFAULT_USER", USERNAME)
            .withEnv("RABBITMQ_DEFAULT_PASS", PASSWORD)
            .withExposedPorts(5672)
            .waitingFor(Wait.forLogMessage(".*Server startup complete.*", 1));

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JsonMapper jsonMapper;

    private CachingConnectionFactory connectionFactory;
    private RabbitTemplate rabbitTemplate;
    private RabbitAdmin rabbitAdmin;
    private TransactionTemplate transactionTemplate;
    private CorrelationIdFilter correlationIdFilter;
    private OutboxEventService outboxEventService;
    private OutboxPublisher outboxPublisher;
    private EventConsumer eventConsumer;
    private Logger consumerLogger;
    private ListAppender<ILoggingEvent> logAppender;

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        outboxEventRepository.deleteAll();
        connectionFactory = createConnectionFactory();
        rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new JacksonJsonMessageConverter(jsonMapper));
        rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareExchange(new TopicExchange(ERP_EXCHANGE, true, false));

        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        correlationIdFilter = new CorrelationIdFilter();
        ReflectionTestUtils.setField(correlationIdFilter, "serviceName", "web-api");
        outboxEventService = new OutboxEventService(outboxEventRepository, jsonMapper);
        outboxPublisher = new OutboxPublisher(outboxEventRepository, rabbitTemplate);
        eventConsumer = new EventConsumer(mock(DocumentPostingService.class), mock(MeterRegistry.class));

        consumerLogger = (Logger) LoggerFactory.getLogger(EventConsumer.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        consumerLogger.addAppender(logAppender);
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        consumerLogger.detachAppender(logAppender);
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
        MDC.clear();
    }

    @Test
    void knownRequestCorrelationId_shouldRemainConsistentAcrossOutboxRabbitAndConsumerLog() throws Exception {
        verifyCorrelationIdFlow("known-correlation-id", "known-correlation-id", true);
    }

    @Test
    void missingRequestCorrelationId_shouldGenerateAndPropagateOneCorrelationId() throws Exception {
        verifyCorrelationIdFlow(null, null, true);
    }

    @Test
    void businessFailure_shouldKeepRequestCorrelationIdThroughRabbitAndConsumerLog() throws Exception {
        verifyCorrelationIdFlow("business-failure-correlation", "business-failure-correlation", false);
    }

    @Test
    void invalidRequestCorrelationId_shouldBeRejectedWithoutCreatingAnOutboxEvent() throws Exception {
        String queueName = declareStockPostedQueue();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new TestEndpoint())
                .addFilters(correlationIdFilter)
                .build();

        MDC.put(CORRELATION_ID_HEADER, "stale-request-correlation");
        mockMvc.perform(get("/correlation-test")
                        .header(CORRELATION_ID_HEADER, "invalid correlation id!"))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist(CORRELATION_ID_HEADER));

        assertThat(outboxEventRepository.findAll()).isEmpty();
        assertThat(rabbitTemplate.receive(queueName, 100)).isNull();
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isNull();
        assertThat(logAppender.list).isEmpty();
    }

    @Test
    void publisher_shouldPublishEventWithoutCorrelationId() {
        String queueName = declareStockPostedQueue();
        MDC.remove(CORRELATION_ID_HEADER);
        transactionTemplate.executeWithoutResult(status ->
                outboxEventService.createOutboxEvent(
                        new StockUpdatedEvent(
                                false, 987654321L, "PRODUCT_APPROVE", "No correlation ID", List.of()),
                        "PRODUCT_APPROVE_POSTED",
                        "PRODUCT_APPROVE",
                        "987654321",
                        STOCK_POSTED_KEY));
        OutboxEvent persistedEvent = outboxEventRepository.findAll().getFirst();
        assertThat(persistedEvent.getCorrelationId()).isNull();
        MDC.put(CORRELATION_ID_HEADER, "stale-publisher-correlation");

        transactionTemplate.executeWithoutResult(status -> outboxPublisher.publishEvents());

        assertThat(outboxEventRepository.findById(persistedEvent.getId()).orElseThrow().getStatus())
                .isEqualTo("PUBLISHED");
        EventMessage eventMessage = receiveEvent(queueName);
        assertThat(eventMessage.eventId()).isEqualTo(persistedEvent.getId());
        assertThat(eventMessage.correlationId()).isNull();
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isNull();
    }

    @Test
    void messageWithoutCorrelationId_shouldClearStaleMdcOnReusedConsumerThread() {
        MDC.put(CORRELATION_ID_HEADER, "stale-correlation-id");
        EventMessage eventMessage = new EventMessage(
                987654321L,
                "PRODUCT_APPROVE_POSTED",
                "PRODUCT_APPROVE",
                "987654321",
                null,
                "{}",
                null);

        eventConsumer.processEvent(eventMessage);

        assertThat(logAppender.list)
                .anySatisfy(event -> {
                    assertThat(event.getFormattedMessage()).contains("Message received");
                    assertThat(event.getMDCPropertyMap()).doesNotContainKey(CORRELATION_ID_HEADER);
                });
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isNull();
    }

    private void verifyCorrelationIdFlow(
            String requestCorrelationId,
            String expectedCorrelationId,
            boolean success) throws Exception {
        String queueName = declareStockPostedQueue();
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (requestCorrelationId != null) {
            request.addHeader(CORRELATION_ID_HEADER, requestCorrelationId);
        }
        MockHttpServletResponse response = new MockHttpServletResponse();

        correlationIdFilter.doFilter(request, response, createOutboxChain());

        String correlationId = response.getHeader(CORRELATION_ID_HEADER);
        assertThat(correlationId).isNotBlank();
        if (expectedCorrelationId != null) {
            assertThat(correlationId).isEqualTo(expectedCorrelationId);
        } else {
            assertThat(correlationId).matches("^web-api_[a-zA-Z0-9-]+$");
        }
        assertThat(request.getAttribute(CORRELATION_ID_HEADER)).isEqualTo(correlationId);
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isNull();

        OutboxEvent persistedEvent = outboxEventRepository.findAll().stream()
                .filter(event -> DOCUMENT_POSTED_KEY.equals(event.getRoutingKey()))
                .findFirst()
                .orElseThrow();
        assertThat(persistedEvent.getCorrelationId()).isEqualTo(correlationId);

        transactionTemplate.executeWithoutResult(status -> outboxPublisher.publishEvents());
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isNull();
        assertThat(outboxEventRepository.findById(persistedEvent.getId()).orElseThrow().getStatus())
                .isEqualTo("PUBLISHED");

        EventMessage stockRequest = receiveEvent(queueName);
        assertThat(stockRequest.eventId()).isEqualTo(persistedEvent.getId());
        assertThat(stockRequest.eventType()).isEqualTo("SALES_ORDER_POSTED");
        assertThat(stockRequest.correlationId()).isEqualTo(correlationId);

        MDC.put(CORRELATION_ID_HEADER, stockRequest.correlationId());
        try {
            StockUpdatedEvent stockResponse = new StockUpdatedEvent(
                    success,
                    987654321L,
                    "SALES_ORDER",
                    success ? "Stock updated successfully" : "Insufficient stock",
                    List.of());
            transactionTemplate.executeWithoutResult(status ->
                    outboxEventService.createOutboxEvent(
                            stockResponse,
                            "SALES_ORDER_POSTED",
                            "SALES_ORDER",
                            "987654321",
                            STOCK_POSTED_KEY));
        } finally {
            MDC.remove(CORRELATION_ID_HEADER);
        }
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isNull();

        OutboxEvent responseEvent = outboxEventRepository.findAll().stream()
                .filter(event -> STOCK_POSTED_KEY.equals(event.getRoutingKey()))
                .findFirst()
                .orElseThrow();
        assertThat(responseEvent.getCorrelationId()).isEqualTo(correlationId);
        transactionTemplate.executeWithoutResult(status -> outboxPublisher.publishEvents());
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isNull();
        assertThat(outboxEventRepository.findById(responseEvent.getId()).orElseThrow().getStatus())
                .isEqualTo("PUBLISHED");

        EventMessage eventMessage = receiveEvent(queueName);
        assertThat(eventMessage.eventId()).isEqualTo(responseEvent.getId());
        assertThat(eventMessage.eventType()).isEqualTo("SALES_ORDER_POSTED");
        assertThat(eventMessage.correlationId()).isEqualTo(correlationId);
        assertThat(eventMessage.payload()).contains("\"success\":" + success);

        logAppender.list.clear();
        eventConsumer.processEvent(eventMessage);

        assertThat(logAppender.list)
                .anySatisfy(event -> {
                    assertThat(event.getFormattedMessage()).contains("Message received");
                    assertThat(event.getMDCPropertyMap()).containsEntry(CORRELATION_ID_HEADER, correlationId);
                });
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isNull();
    }

    private FilterChain createOutboxChain() {
        StockMovementCommand payload = new StockMovementCommand(
                987654321L,
                "SALES_ORDER",
                List.of());
        return (request, response) -> transactionTemplate.executeWithoutResult(status ->
                outboxEventService.createOutboxEvent(
                        payload,
                        "SALES_ORDER_POSTED",
                        "SALES_ORDER",
                        "987654321",
                        DOCUMENT_POSTED_KEY));
    }

    private String declareStockPostedQueue() {
        String queueName = "correlation-flow-" + UUID.randomUUID();
        Queue queue = QueueBuilder.durable(queueName).build();
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue)
                .to(new TopicExchange(ERP_EXCHANGE))
                .with(DOCUMENT_POSTED_KEY));
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue)
                .to(new TopicExchange(ERP_EXCHANGE))
                .with(STOCK_POSTED_KEY));
        return queueName;
    }

    private EventMessage receiveEvent(String queueName) {
        Message received = rabbitTemplate.receive(queueName, 1_000);
        assertThat(received).isNotNull();
        return jsonMapper.readValue(received.getBody(), EventMessage.class);
    }

    private CachingConnectionFactory createConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(
                rabbit.getHost(), rabbit.getMappedPort(5672));
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        factory.setPublisherReturns(true);
        return factory;
    }

    @RestController
    static class TestEndpoint {
        @GetMapping("/correlation-test")
        String handle() {
            return "ok";
        }
    }
}

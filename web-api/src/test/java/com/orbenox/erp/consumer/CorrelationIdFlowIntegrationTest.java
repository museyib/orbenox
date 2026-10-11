package com.orbenox.erp.consumer;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.orbenox.erp.config.CorrelationIdFilter;
import com.orbenox.erp.config.JpaAuditingConfig;
import com.orbenox.erp.message.EventMessage;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.outbox.OutboxEvent;
import com.orbenox.erp.outbox.OutboxEventRepository;
import com.orbenox.erp.outbox.OutboxEventService;
import com.orbenox.erp.outbox.OutboxPublisher;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
import org.springframework.amqp.core.Message;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.ERP_EXCHANGE;
import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.STOCK_POSTED_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

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
        eventConsumer = new EventConsumer(mock(DocumentPostingService.class));

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
        verifyCorrelationIdFlow("known-correlation-id", "known-correlation-id");
    }

    @Test
    void missingRequestCorrelationId_shouldGenerateAndPropagateOneCorrelationId() throws Exception {
        verifyCorrelationIdFlow(null, null);
    }

    @Test
    void invalidRequestCorrelationId_shouldBeRejectedWithoutCreatingAnOutboxEvent() throws Exception {
        String queueName = declareStockPostedQueue();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CORRELATION_ID_HEADER, "invalid correlation id!");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThatThrownBy(() -> correlationIdFilter.doFilter(request, response, createOutboxChain()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid correlation ID");

        assertThat(response.getHeader(CORRELATION_ID_HEADER)).isNull();
        assertThat(request.getAttribute(CORRELATION_ID_HEADER)).isNull();
        assertThat(outboxEventRepository.findAll()).isEmpty();
        assertThat(rabbitTemplate.receive(queueName, 100)).isNull();
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isNull();
        assertThat(logAppender.list).isEmpty();
    }

    private void verifyCorrelationIdFlow(String requestCorrelationId, String expectedCorrelationId) throws Exception {
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
                .filter(event -> "PRODUCT_APPROVE_POSTED".equals(event.getEventType()))
                .findFirst()
                .orElseThrow();
        assertThat(persistedEvent.getCorrelationId()).isEqualTo(correlationId);

        transactionTemplate.executeWithoutResult(status -> outboxPublisher.publishEvents());
        assertThat(MDC.get(CORRELATION_ID_HEADER)).isNull();
        assertThat(outboxEventRepository.findById(persistedEvent.getId()).orElseThrow().getStatus())
                .isEqualTo("PUBLISHED");

        Message publishedMessage = rabbitTemplate.receive(queueName, 1_000);
        assertThat(publishedMessage).isNotNull();
        EventMessage eventMessage = jsonMapper.readValue(publishedMessage.getBody(), EventMessage.class);
        assertThat(eventMessage.eventId()).isEqualTo(persistedEvent.getId());
        assertThat(eventMessage.correlationId()).isEqualTo(correlationId);

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
        StockUpdatedEvent payload = new StockUpdatedEvent(
                false,
                987654321L,
                "PRODUCT_APPROVE",
                "Correlation integration test",
                List.of());
        return (request, response) -> transactionTemplate.executeWithoutResult(status ->
                outboxEventService.createOutboxEvent(
                        payload,
                        "PRODUCT_APPROVE_POSTED",
                        "PRODUCT_APPROVE",
                        "987654321",
                        STOCK_POSTED_KEY));
    }

    private String declareStockPostedQueue() {
        String queueName = "correlation-flow-" + UUID.randomUUID();
        Queue queue = QueueBuilder.durable(queueName).build();
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue)
                .to(new TopicExchange(ERP_EXCHANGE))
                .with(STOCK_POSTED_KEY));
        return queueName;
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
}

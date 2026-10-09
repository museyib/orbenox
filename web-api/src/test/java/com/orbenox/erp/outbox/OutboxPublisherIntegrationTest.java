package com.orbenox.erp.outbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.ERP_EXCHANGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class OutboxPublisherIntegrationTest {

    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";

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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JsonMapper jsonMapper;

    private CachingConnectionFactory connectionFactory;
    private RabbitTemplate rabbitTemplate;
    private RabbitAdmin rabbitAdmin;
    private TransactionTemplate transactionTemplate;

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        outboxEventRepository.deleteAll();
        connectionFactory = createConnectionFactory(rabbit.getHost(), rabbit.getMappedPort(5672));
        rabbitTemplate = createRabbitTemplate(connectionFactory);
        rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareExchange(new TopicExchange(ERP_EXCHANGE, true, false));
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    @AfterEach
    void tearDown() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    @Test
    void brokerAckWithoutReturn_shouldMarkEventPublished() throws Exception {
        String routingKey = uniqueRoutingKey();
        String queueName = declareBoundQueue(routingKey);
        OutboxEvent event = savePendingEvent(routingKey);

        publish(new OutboxPublisher(outboxEventRepository, rabbitTemplate));

        assertThat(loadEvent(event).getStatus()).isEqualTo("PUBLISHED");
        assertThat(rabbitTemplate.receive(queueName, 1_000)).isNotNull();
    }

    @Test
    void brokerNack_shouldLeaveEventPending() throws Exception {
        String routingKey = uniqueRoutingKey();
        String queueName = declareRejectingQueue(routingKey);
        OutboxEvent event = savePendingEvent(routingKey);
        CorrelationData fillerConfirmation = new CorrelationData("filler-" + event.getId());
        rabbitTemplate.convertAndSend(ERP_EXCHANGE, routingKey, "filler", fillerConfirmation);
        assertThat(fillerConfirmation.getFuture().get(5, TimeUnit.SECONDS).ack()).isTrue();

        publish(new OutboxPublisher(outboxEventRepository, rabbitTemplate));

        assertThat(loadEvent(event).getStatus()).isEqualTo("PENDING");
        assertThat(rabbitTemplate.receive(queueName, 1_000)).isNotNull();
        assertThat(rabbitTemplate.receive(queueName, 100)).isNull();
    }

    @Test
    void returnedUnroutableMessage_shouldLeaveEventPending() {
        String routingKey = uniqueRoutingKey();
        OutboxEvent event = savePendingEvent(routingKey);

        publish(new OutboxPublisher(outboxEventRepository, rabbitTemplate));

        assertThat(loadEvent(event).getStatus()).isEqualTo("PENDING");
    }

    @Test
    void confirmationTimeout_shouldLeaveEventPending() {
        OutboxEvent event = savePendingEvent(uniqueRoutingKey());
        RabbitTemplate templateWithoutConfirmation = mock(RabbitTemplate.class);
        doNothing().when(templateWithoutConfirmation)
                .convertAndSend(anyString(), anyString(), any(EventMessage.class), any(CorrelationData.class));

        publish(new OutboxPublisher(outboxEventRepository, templateWithoutConfirmation));

        assertThat(loadEvent(event).getStatus()).isEqualTo("PENDING");
    }

    @Test
    void databaseFailureAfterBrokerAck_shouldRollbackStatusAndAllowDuplicatePublication() {
        String routingKey = uniqueRoutingKey();
        String queueName = declareBoundQueue(routingKey);
        OutboxEvent event = savePendingEvent(routingKey);
        jdbcTemplate.execute("""
                CREATE FUNCTION fail_outbox_published_update() RETURNS trigger AS $$
                BEGIN
                    IF NEW.status = 'PUBLISHED' THEN
                        RAISE EXCEPTION 'simulated outbox status update failure';
                    END IF;
                    RETURN NEW;
                END;
                $$ LANGUAGE plpgsql
                """);
        jdbcTemplate.execute("""
                CREATE TRIGGER fail_outbox_published_update_trigger
                BEFORE UPDATE ON outbox_event
                FOR EACH ROW EXECUTE FUNCTION fail_outbox_published_update()
                """);

        try {
            assertThatThrownBy(() -> publish(new OutboxPublisher(outboxEventRepository, rabbitTemplate)))
                    .isInstanceOf(RuntimeException.class);

            assertThat(loadEvent(event).getStatus()).isEqualTo("PENDING");
            assertThat(rabbitTemplate.receive(queueName, 1_000)).isNotNull();
        } finally {
            jdbcTemplate.execute("DROP TRIGGER IF EXISTS fail_outbox_published_update_trigger ON outbox_event");
            jdbcTemplate.execute("DROP FUNCTION IF EXISTS fail_outbox_published_update()");
        }

        publish(new OutboxPublisher(outboxEventRepository, rabbitTemplate));
        assertThat(loadEvent(event).getStatus()).isEqualTo("PUBLISHED");
        assertThat(rabbitTemplate.receive(queueName, 1_000)).isNotNull();
    }

    @Test
    void rabbitUnavailable_shouldRetainEventForRetry() {
        OutboxEvent event = savePendingEvent(uniqueRoutingKey());
        CachingConnectionFactory unavailableConnectionFactory = createConnectionFactory("127.0.0.1", 1);
        unavailableConnectionFactory.setConnectionTimeout(500);
        RabbitTemplate unavailableTemplate = createRabbitTemplate(unavailableConnectionFactory);

        try {
            publish(new OutboxPublisher(outboxEventRepository, unavailableTemplate));
            assertThat(loadEvent(event).getStatus()).isEqualTo("PENDING");
        } finally {
            unavailableConnectionFactory.destroy();
        }
    }

    private void publish(OutboxPublisher publisher) {
        transactionTemplate.executeWithoutResult(status -> publisher.publishEvents());
    }

    private OutboxEvent savePendingEvent(String routingKey) {
        OutboxEvent event = new OutboxEvent();
        event.setEventType("STOCK_POSTED");
        event.setAggregateType("DOCUMENT");
        event.setAggregateId(UUID.randomUUID().toString());
        event.setAggregateVersion("1");
        event.setPayload("{\"success\":true}");
        event.setStatus("PENDING");
        event.setRoutingKey(routingKey);
        event.setCreatedAt(LocalDateTime.now());
        return outboxEventRepository.saveAndFlush(event);
    }

    private OutboxEvent loadEvent(OutboxEvent event) {
        return outboxEventRepository.findById(event.getId()).orElseThrow();
    }

    private String declareBoundQueue(String routingKey) {
        String queueName = "outbox-test-" + UUID.randomUUID();
        Queue queue = QueueBuilder.durable(queueName).build();
        rabbitAdmin.declareQueue(queue);
        Binding binding = BindingBuilder.bind(queue).to(new TopicExchange(ERP_EXCHANGE)).with(routingKey);
        rabbitAdmin.declareBinding(binding);
        return queueName;
    }

    private String declareRejectingQueue(String routingKey) {
        String queueName = "outbox-reject-" + UUID.randomUUID();
        Queue queue = QueueBuilder.durable(queueName)
                .withArgument("x-max-length", 1)
                .withArgument("x-overflow", "reject-publish")
                .build();
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue)
                .to(new TopicExchange(ERP_EXCHANGE))
                .with(routingKey));
        return queueName;
    }

    private String uniqueRoutingKey() {
        return "outbox.test." + UUID.randomUUID();
    }

    private CachingConnectionFactory createConnectionFactory(String host, int port) {
        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        factory.setPublisherReturns(true);
        return factory;
    }

    private RabbitTemplate createRabbitTemplate(CachingConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMandatory(true);
        template.setMessageConverter(new JacksonJsonMessageConverter(jsonMapper));
        return template;
    }
}

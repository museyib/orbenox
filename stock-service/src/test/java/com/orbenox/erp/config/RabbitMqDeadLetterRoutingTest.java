package com.orbenox.erp.config;

import com.orbenox.erp.consumer.EventConsumer;
import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.outbox.EventMessage;
import com.orbenox.erp.service.StockPostingService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testcontainers
class RabbitMqDeadLetterRoutingTest {
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";
    private static final String PAYLOAD = "malformed stock command";

    @Container
    private static final GenericContainer<?> rabbit = new GenericContainer<>(
            DockerImageName.parse("rabbitmq:3.13-alpine"))
            .withEnv("RABBITMQ_DEFAULT_USER", USERNAME)
            .withEnv("RABBITMQ_DEFAULT_PASS", PASSWORD)
            .withExposedPorts(5672)
            .waitingFor(Wait.forLogMessage(".*Server startup complete.*", 1));

    @Test
    void rejectedMessageFromMainQueue_shouldBeRoutedToDeadLetterQueue() throws Exception {

        RabbitMqInfrastructureConfiguration infrastructureConfiguration = new RabbitMqInfrastructureConfiguration();
        RabbitMqConfiguration configuration = new RabbitMqConfiguration(infrastructureConfiguration);
        Queue mainQueue = configuration.documentPostedQueue();
        Queue deadLetterQueue = configuration.documentPostedDlq();
        CachingConnectionFactory connectionFactory = createConnectionFactory();

        try {
            declareTopology(infrastructureConfiguration, configuration, connectionFactory);
            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.convertAndSend(ERP_EXCHANGE, mainQueue.getName(), PAYLOAD);

            var connection = connectionFactory.createConnection();
            try (var channel = connection.createChannel(false)) {
                var received = channel.basicGet(mainQueue.getName(), false);
                assertThat(received).isNotNull();
                channel.basicReject(received.getEnvelope().getDeliveryTag(), false);
            } finally {
                connection.close();
            }

            assertDeadLetter(rabbitTemplate.receive(deadLetterQueue.getName(), 5_000), mainQueue);
        } finally {
            connectionFactory.destroy();
        }
    }

    @Test
    void repeatedTechnicalFailure_shouldRetryThenDeadLetterCommand() {
        RabbitMqInfrastructureConfiguration infrastructureConfiguration = new RabbitMqInfrastructureConfiguration();
        RabbitMqConfiguration configuration = new RabbitMqConfiguration(infrastructureConfiguration);
        Queue mainQueue = configuration.documentPostedQueue();
        Queue deadLetterQueue = configuration.documentPostedDlq();
        CachingConnectionFactory connectionFactory = createConnectionFactory();
        StockPostingService stockService = mock(StockPostingService.class);
        StockMovementCommand command = new StockMovementCommand(
                12L, "SO-12", List.of(
                new StockMovementCommand.StockOperation(11L, 1L, 1L, BigDecimal.TEN, 1),
                new StockMovementCommand.StockOperation(12L, 2L, 1L, BigDecimal.TEN, 1)
        ));
        doThrow(new IllegalStateException("Database unavailable")).when(stockService).post(command);
        SimpleMessageListenerContainer listener = null;

        EventMessage eventMessage = new EventMessage(
              1L,
                "STOCK_POSTED",
                "PRODUCT_APPROVE",
                "12",
                "",
                new JsonMapper().writeValueAsString(command)
        );

        try {
            declareTopology(infrastructureConfiguration, configuration, connectionFactory);
            listener = new SimpleMessageListenerContainer(connectionFactory);
            listener.setQueueNames(mainQueue.getName());
            listener.setMessageListener(_ ->
                    new EventConsumer(stockService, new JsonMapper()).processEvent(eventMessage));
            listener.setAdviceChain(RetryInterceptorBuilder.stateless()
                    .maxRetries(3)
                    .backOffOptions(1, 1.0, 1)
                    .recoverer(new RejectAndDontRequeueRecoverer())
                    .build());
            listener.start();

            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.convertAndSend(ERP_EXCHANGE, mainQueue.getName(), PAYLOAD);
            Message deadLetter = rabbitTemplate.receive(deadLetterQueue.getName(), 10_000);

            assertDeadLetter(deadLetter, mainQueue);
            verify(stockService, times(4)).post(command);
        } finally {
            if (listener != null) {
                listener.stop();
            }
            connectionFactory.destroy();
        }
    }

    @Test
    void mainQueue_shouldDeclareDeadLetterExchangeAndRoutingKey() {
        RabbitMqInfrastructureConfiguration infrastructureConfiguration = new RabbitMqInfrastructureConfiguration();
        RabbitMqConfiguration configuration = new RabbitMqConfiguration(infrastructureConfiguration);
        Queue mainQueue = configuration.documentPostedQueue();

        assertThat(mainQueue.getArguments())
                .containsEntry("x-dead-letter-exchange", DLX_EXCHANGE)
                .containsEntry("x-dead-letter-routing-key", DOCUMENT_POSTED_DLQ_KEY);
        assertThat(configuration.documentPostedDlqBinding().getDestination())
                .isEqualTo(DOCUMENT_POSTED_DLQ);
        assertThat(configuration.documentPostedDlqBinding().getRoutingKey())
                .isEqualTo(DOCUMENT_POSTED_DLQ_KEY);
    }

    private CachingConnectionFactory createConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
                rabbit.getHost(), rabbit.getMappedPort(5672));
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        return connectionFactory;
    }

    private void declareTopology(RabbitMqInfrastructureConfiguration infrastructureConfiguration,
                                 RabbitMqConfiguration configuration,
                                CachingConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        TopicExchange mainExchange = infrastructureConfiguration.erpExchange();
        DirectExchange deadLetterExchange = infrastructureConfiguration.deadLetterExchange();
        rabbitAdmin.declareExchange(mainExchange);
        rabbitAdmin.declareExchange(deadLetterExchange);
        rabbitAdmin.declareQueue(configuration.documentPostedQueue());
        rabbitAdmin.declareQueue(configuration.documentPostedDlq());
        rabbitAdmin.declareBinding(configuration.documentPostedDlqBinding());
    }

    private void assertDeadLetter(Message deadLetter, Queue mainQueue) {
        assertThat(deadLetter).isNotNull();
        assertThat(new String(deadLetter.getBody(), StandardCharsets.UTF_8)).isEqualTo(PAYLOAD);
        Object deathHeader = deadLetter.getMessageProperties().getHeaders().get("x-death");
        assertThat(deathHeader).isInstanceOf(List.class);
        assertThat((List<?>) deathHeader).singleElement().satisfies(death -> {
            assertThat(death).isInstanceOf(java.util.Map.class);
            java.util.Map<?, ?> deathProperties = (java.util.Map<?, ?>) death;
            assertThat(deathProperties.get("queue")).isEqualTo(mainQueue.getName());
            assertThat(deathProperties.get("reason")).isEqualTo("rejected");
        });
    }
}

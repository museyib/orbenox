package com.orbenox.erp.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitMqConfigurationTest {

    @Test
    void stockPostedQueue_shouldRouteRejectedMessagesToDeadLetterQueue() {
        RabbitMqConfiguration configuration = new RabbitMqConfiguration();
        Queue queue = configuration.stockPostedQueue();

        assertThat(queue.getName()).isEqualTo(RabbitMqConfiguration.STOCK_POSTED_QUEUE);
        assertThat(queue.getArguments())
                .containsEntry("x-dead-letter-exchange", RabbitMqConfiguration.DLX_EXCHANGE)
                .containsEntry("x-dead-letter-routing-key", RabbitMqConfiguration.DLQ_ROUTING_KEY);
        assertThat(configuration.dlqBinding().getDestination())
                .isEqualTo(RabbitMqConfiguration.DLQ_QUEUE);
        assertThat(configuration.dlqBinding().getRoutingKey())
                .isEqualTo(RabbitMqConfiguration.DLQ_ROUTING_KEY);
    }
}

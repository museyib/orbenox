package com.orbenox.erp.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;

class RabbitMqConfigurationTest {

    @Test
    void stockPostedQueue_shouldRouteRejectedMessagesToDeadLetterQueue() {
        RabbitMqInfrastructureConfiguration infrastructureConfiguration = new RabbitMqInfrastructureConfiguration();
        RabbitMqConfiguration configuration = new RabbitMqConfiguration(infrastructureConfiguration);
        Queue queue = configuration.stockPostedQueue();

        assertThat(queue.getName()).isEqualTo(STOCK_POSTED_QUEUE);
        assertThat(queue.getArguments())
                .containsEntry("x-dead-letter-exchange", DLX_EXCHANGE)
                .containsEntry("x-dead-letter-routing-key", STOCK_POSTED_DLQ_KEY);
        assertThat(configuration.stockPostedDlqBinding().getDestination())
                .isEqualTo(STOCK_POSTED_DLQ);
        assertThat(configuration.stockPostedDlqBinding().getRoutingKey())
                .isEqualTo(STOCK_POSTED_DLQ_KEY);
    }
}

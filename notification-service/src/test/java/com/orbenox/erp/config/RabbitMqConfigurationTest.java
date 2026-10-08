package com.orbenox.erp.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;

class RabbitMqConfigurationTest {

    @Test
    void notificationQueue_shouldRouteRejectedMessagesToDeadLetterQueue() {
        RabbitMqInfrastructureConfiguration infrastructureConfiguration = new RabbitMqInfrastructureConfiguration();
        RabbitMqConfiguration configuration = new RabbitMqConfiguration(infrastructureConfiguration);
        Queue queue = configuration.notificationQueue();

        assertThat(queue.getName()).isEqualTo(NOTIFICATION_QUEUE);
        assertThat(queue.getArguments())
                .containsEntry("x-dead-letter-exchange", DLX_EXCHANGE)
                .containsEntry("x-dead-letter-routing-key", NOTIFICATION_DLQ_KEY);
        assertThat(configuration.notificationDlqBinding().getDestination())
                .isEqualTo(NOTIFICATION_DLQ);
        assertThat(configuration.notificationDlqBinding().getRoutingKey())
                .isEqualTo(NOTIFICATION_DLQ_KEY);
    }
}

package com.orbenox.erp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.*;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfiguration {
    private final RabbitMqInfrastructureConfiguration infrastructureConfiguration;

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding notificationDocumentCreatedBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(infrastructureConfiguration.erpExchange())
                .with("document.created");
    }

    @Bean
    public Binding notificationDocumentPostedBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(infrastructureConfiguration.erpExchange())
                .with("document.posted");
    }

    @Bean
    public Binding notificationStockPostedBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(infrastructureConfiguration.erpExchange())
                .with("stock.posted");
    }
}

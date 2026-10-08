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
    public Queue stockPostQueue() {
        return QueueBuilder.durable(STOCK_POST_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }
    @Bean
    public Queue stockPostedQueue() {
        return QueueBuilder.durable(STOCK_POSTED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding stockPostBinding() {
        return BindingBuilder.bind(stockPostQueue())
                .to(infrastructureConfiguration.erpExchange()).with(MAIN_ROUTING_KEY);
    }

    @Bean
    public Binding stockPostedBinding() {
        return BindingBuilder.bind(stockPostedQueue())
                .to(infrastructureConfiguration.erpExchange()).with(MAIN_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(infrastructureConfiguration.erpExchange()).with(MAIN_ROUTING_KEY);
    }
}

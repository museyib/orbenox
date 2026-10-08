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
    public Queue documentPostedQueue() {
        return QueueBuilder.durable(DOCUMENT_POSTED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding documentPostedBinding() {
        return BindingBuilder.bind(documentPostedQueue())
                .to(infrastructureConfiguration.erpExchange())
                .with(DOCUMENT_POSTED_KEY);
    }

    @Bean
    public Queue stockPostedQueue() {
        return QueueBuilder.durable(STOCK_POSTED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding stockPostedBinding() {
        return BindingBuilder.bind(stockPostedQueue())
                .to(infrastructureConfiguration.erpExchange())
                .with(STOCK_POSTED_KEY);
    }
}

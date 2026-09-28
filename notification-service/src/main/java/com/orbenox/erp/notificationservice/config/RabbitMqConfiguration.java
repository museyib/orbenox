package com.orbenox.erp.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    @Bean
    public TopicExchange outboxExchange() {
        return new TopicExchange("outbox-exchange");
    }

    @Bean
    public Queue outboxQueue() {
        return new Queue("notification-queue");
    }

    @Bean
    public Binding outboxBinding(Queue outboxQueue, TopicExchange outboxExchange) {
        return BindingBuilder.bind(outboxQueue).to(outboxExchange).with("outbox-routing-key");
    }
}

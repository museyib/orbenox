package com.orbenox.erp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMqInfrastructureConfiguration {
    public static final String MAIN_EXCHANGE = "erp.exchange";
    public static final String MAIN_ROUTING_KEY = "erp.routing.key";

    public static final String STOCK_POST_QUEUE = "stock.post";
    public static final String STOCK_POSTED_QUEUE = "stock.posted";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    public static final String DLQ_QUEUE = "dead.letter.queue";
    public static final String DLX_EXCHANGE = "dead.letter.exchange";
    public static final String DLQ_ROUTING_KEY = "dead.letter.routing.key";

    @Bean
    public TopicExchange erpExchange() {
        return new TopicExchange(MAIN_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}

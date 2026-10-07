package com.orbenox.erp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMqConfiguration {
    public static final String MAIN_EXCHANGE = "outbox.exchange";
    public static final String MAIN_ROUTING_KEY = "outbox.routing.key";
    public static final String MAIN_QUEUE = "stock.posted";

    public static final String DLQ_QUEUE = "dead.letter.queue";
    public static final String DLX_EXCHANGE = "dead.letter.exchange";
    public static final String DLQ_ROUTING_KEY = "dead.letter.routing.key";

    @Bean
    public TopicExchange outboxExchange() {
        return new TopicExchange(MAIN_EXCHANGE);
    }

    @Bean
    public Queue stockPostQueue() {
        return QueueBuilder.durable(MAIN_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding outboxBinding() {
        return BindingBuilder.bind(stockPostQueue()).to(outboxExchange()).with(MAIN_ROUTING_KEY);
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

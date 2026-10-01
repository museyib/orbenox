package com.orbenox.erp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMqConfiguration {

    @Bean
    public TopicExchange outboxExchange() {
        return new TopicExchange("outbox-exchange");
    }

    @Bean
    public Queue stockPostQueue() {
        return QueueBuilder.durable("stock.post")
                .withArgument("x-dead-letter-exchange", "dlx")
                .withArgument("x-dead-letter-routing-key", "dlx-routing-key")
                .build();
    }

    @Bean
    public Binding outboxBinding(Queue outboxQueue, TopicExchange outboxExchange) {
        return BindingBuilder.bind(outboxQueue).to(outboxExchange).with("outbox-routing-key");
    }

    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}

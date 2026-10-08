package com.orbenox.erp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMqInfrastructureConfiguration {
    public static final String ERP_EXCHANGE = "erp.exchange";
    public static final String DLX_EXCHANGE = "dead.letter.exchange";

    public static final String DOCUMENT_CREATED_KEY = "document.created";
    public static final String DOCUMENT_POSTED_KEY = "document.posted";
    public static final String STOCK_POSTED_KEY = "stock.posted";

    public static final String DOCUMENT_POSTED_QUEUE = "document.posted.queue";
    public static final String STOCK_POSTED_QUEUE = "stock.posted.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    public static final String DOCUMENT_POSTED_DLQ = "document.posted.dlq";
    public static final String STOCK_POSTED_DLQ = "stock.posted.dlq";
    public static final String NOTIFICATION_DLQ = "notification.dlq";


    public static final String DOCUMENT_POSTED_DLQ_KEY = "document.posted.dlq.key";
    public static final String STOCK_POSTED_DLQ_KEY = "stock.posted.dlq.key";
    public static final String NOTIFICATION_DLQ_KEY = "notification.dlq.key";

    @Bean
    public TopicExchange erpExchange() {
        return new TopicExchange(ERP_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}

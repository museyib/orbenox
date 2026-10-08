package com.orbenox.erp.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.NOTIFICATION_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final InboxEventRepository inboxEventRepository;
    private final JsonMapper jsonMapper;

    @RabbitListener(queues = NOTIFICATION_QUEUE)
    @Transactional
    public void processEvent(String message) {
        try {
            EventMessage eventMessage = jsonMapper.readValue(message, EventMessage.class);
            int affected = inboxEventRepository.createInboxEvent("notification-service", eventMessage.eventId());
            if (affected > 0) {
                log.info("Message received: {}", message);
            } else {
                log.warn("Event {} already processed", eventMessage.eventId());
            }
        } catch (JacksonException e) {
            log.error("Error processing message: {}", message, e);
            throw new RuntimeException(e);
        }
    }
}

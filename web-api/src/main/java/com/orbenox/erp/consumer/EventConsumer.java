package com.orbenox.erp.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbenox.erp.outbox.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final InboxEventRepository inboxEventRepository;
    private final ObjectMapper jsonMapper;

    @RabbitListener(queues = "outbox-queue")
    public void processEvent(String message) {
        try {
            OutboxEvent outboxEvent = jsonMapper.readValue(message, OutboxEvent.class);
            int affected = inboxEventRepository.createInboxEvent("event_consumer", outboxEvent.getId());
            if (affected > 0) {
                log.info("Message received: {}", message);
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing message: {}", message, e);
        }
    }
}

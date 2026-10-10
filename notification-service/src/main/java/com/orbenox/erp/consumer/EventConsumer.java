package com.orbenox.erp.consumer;

import com.orbenox.erp.message.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.NOTIFICATION_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final InboxEventRepository inboxEventRepository;

    @RabbitListener(queues = NOTIFICATION_QUEUE)
    @Transactional
    public void processEvent(EventMessage eventMessage) {
        log.info("Message received: {}", eventMessage);
        try {
            int affected = inboxEventRepository.createInboxEvent("notification-service", eventMessage.eventId());
            if (affected > 0) {
                log.info("Event {} processed", eventMessage.eventId());
            } else {
                log.warn("Event {} already processed", eventMessage.eventId());
            }
        } catch (JacksonException e) {
            log.error("Error processing message: {}", eventMessage, e);
            throw new RuntimeException(e);
        }
    }
}

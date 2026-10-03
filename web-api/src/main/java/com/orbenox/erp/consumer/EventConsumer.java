package com.orbenox.erp.consumer;

import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.outbox.EventMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final DocumentPostingService documentPostingService;
    private final InboxEventRepository inboxEventRepository;
    private final JsonMapper jsonMapper;

    @Transactional
    @RabbitListener(queues = "stock.posted")
    public void processEvent(EventMessage eventMessage) {

        log.info("Message received: {}", eventMessage);

        StockUpdatedEvent event = jsonMapper.readValue(eventMessage.payload(), StockUpdatedEvent.class);

        int affected = inboxEventRepository.createInboxEvent("web-api", eventMessage.eventId());

        if (affected > 0) {
            if (event.success()) {
                documentPostingService.updateStatus(event.documentId(), event.typeCode(), DocumentStatus.POSTED);
                log.info("Document {} successfully posted", event.documentId());
            } else {
                documentPostingService.updateStatus(event.documentId(), event.typeCode(), DocumentStatus.IN_PROGRESS);
                log.error("Stock posting failed: {}", event.message());
            }

        } else {
            log.warn("Event {} already processed", event.documentId());
        }
    }
}

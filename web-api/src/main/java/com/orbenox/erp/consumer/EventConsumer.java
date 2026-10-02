package com.orbenox.erp.consumer;

import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final DocumentPostingService documentPostingService;

    @RabbitListener(queues = "stock.posted")
    public void processEvent(StockUpdatedEvent event) {
        try {
            log.info("Message received: {}", event);
            if (event.success())
                documentPostingService.updateStatus(event.documentId(), event.typeCode(), DocumentStatus.POSTED);
            else {
                documentPostingService.updateStatus(event.documentId(), event.typeCode(), DocumentStatus.IN_PROGRESS);
                log.error("Error processing message: {}", event);
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", event, e);
        }
    }
}

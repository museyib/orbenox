package com.orbenox.erp.consumer;

import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.transaction.service.SalesOrderActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final SalesOrderActionService stockService;

    @RabbitListener(queues = "stock.posted")
    public void processEvent(StockUpdatedEvent event) {
        try {
            log.info("Message received: {}", event);
            stockService.updateDocumentStatus(event.documentId(), DocumentStatus.POSTED);
        } catch (Exception e) {
            log.error("Error processing message: {}", event, e);
        }
    }
}

package com.orbenox.erp.consumer;

import com.orbenox.erp.messaging.command.PostDocumentCommand;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.outbox.OutboxEventService;
import com.orbenox.erp.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final StockService stockService;
    private final OutboxEventService outboxEventService;

    @RabbitListener(queues = "stock.post")
    public void processEvent(PostDocumentCommand command) {
        log.info("Message received: {}", command);
        StockUpdatedEvent stockUpdatedEvent;

        try {

            stockService.post(command.id());
            stockUpdatedEvent = new StockUpdatedEvent(
                    true,
                    command.id(),
                    command.typeCode(),
                    "Stock updated successfully"
            );
        } catch (Exception e) {
            log.error("Error processing event: {}", e.getMessage());
            stockUpdatedEvent = new StockUpdatedEvent(
                    false,
                    command.id(),
                    command.typeCode(),
                    e.getMessage()
            );
        }
        outboxEventService.createOutboxEvent(stockUpdatedEvent, command.typeCode() + "_POSTED", command.typeCode());
        log.info("Stock updated event created: {}", stockUpdatedEvent);
    }
}

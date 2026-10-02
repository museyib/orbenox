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

    @Transactional
    @RabbitListener(queues = "stock.post")
    public void processEvent(PostDocumentCommand command) {
        log.info("Message received: {}", command);
        stockService.post(command.id());
        StockUpdatedEvent stockUpdatedEvent = new StockUpdatedEvent(
                true,
                command.id(),
                command.typeCode(),
                "Stock updated successfully"
        );
        outboxEventService.createOutboxEvent(stockUpdatedEvent, command.typeCode() + "_POSTED", command.typeCode());
    }
}

package com.orbenox.erp.service;

import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.outbox.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockPostingService {
    private final StockService stockService;
    private final OutboxEventService outboxEventService;

    @Transactional
    public void post(StockMovementCommand command) {

        stockService.updateStock(command);
        StockUpdatedEvent event = new StockUpdatedEvent(
                true,
                command.documentId(),
                command.typeCode(),
                "Stock updated successfully",
                command.operations()
        );

        outboxEventService.createOutboxEvent(
                event,
                command.typeCode() + "_POSTED",
                command.typeCode()
        );
        log.info("Stock updated event created: {}", event);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishFailureEvent(
            StockMovementCommand command,
            String message
    ) {

        StockUpdatedEvent event = new StockUpdatedEvent(
                false,
                command.documentId(),
                command.typeCode(),
                message,
                command.operations()
        );

        outboxEventService.createOutboxEvent(
                event,
                command.typeCode() + "_POSTED",
                command.typeCode()
        );
        log.error("Error processing event: {}", message);
    }
}

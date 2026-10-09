package com.orbenox.erp.service;

import com.orbenox.erp.consumer.InboxEventRepository;
import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.outbox.EventMessage;
import com.orbenox.erp.outbox.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockPostingService {
    private final StockService stockService;
    private final InboxEventRepository inboxEventRepository;
    private final OutboxEventService outboxEventService;
    private final JsonMapper jsonMapper;

    @Transactional
    public void post(EventMessage eventMessage) {

        int affected = inboxEventRepository.createInboxEvent("stock-service", eventMessage.eventId());

        if (affected > 0) {
            StockMovementCommand command = jsonMapper.readValue(eventMessage.payload(), StockMovementCommand.class);

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
        } else {
            throw new RuntimeException(String.format("Event %s already processed", eventMessage.eventId()));
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishFailureEvent(EventMessage eventMessage, String message) {
        StockMovementCommand command = jsonMapper.readValue(eventMessage.payload(), StockMovementCommand.class);

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

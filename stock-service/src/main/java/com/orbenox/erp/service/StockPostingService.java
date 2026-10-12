package com.orbenox.erp.service;

import com.orbenox.erp.consumer.InboxEventRepository;
import com.orbenox.erp.message.EventMessage;
import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.outbox.OutboxEventService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Service
@Slf4j
public class StockPostingService {
    private final Counter eventConsumptionDuplicateCounter;
    private final StockService stockService;
    private final InboxEventRepository inboxEventRepository;
    private final OutboxEventService outboxEventService;
    private final JsonMapper jsonMapper;

    public StockPostingService(StockService stockService,
                               InboxEventRepository inboxEventRepository,
                               OutboxEventService outboxEventService,
                               JsonMapper jsonMapper,
                               MeterRegistry registry) {
        this.eventConsumptionDuplicateCounter = registry.counter("event.consumption.duplicateAttempt");
        this.stockService = stockService;
        this.inboxEventRepository = inboxEventRepository;
        this.outboxEventService = outboxEventService;
        this.jsonMapper = jsonMapper;
    }

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
                    command.typeCode(),
                    eventMessage.correlationId()
            );
            log.info("Stock updated event created: {}", event);
        } else {
            log.warn("Event {} already processed", eventMessage.eventId());
            eventConsumptionDuplicateCounter.increment();
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
                command.typeCode(),
                eventMessage.correlationId()
        );
        log.error("Stock processing failed: {}", message);
    }
}

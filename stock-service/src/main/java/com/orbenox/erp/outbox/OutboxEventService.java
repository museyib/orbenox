package com.orbenox.erp.outbox;

import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final JsonMapper jsonMapper;

    public void createOutboxEvent(StockUpdatedEvent event, String eventType, String aggregateType) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(eventType);
        outboxEvent.setAggregateType(aggregateType);
        outboxEvent.setAggregateId(event.documentId().toString());
        outboxEvent.setPayload(jsonMapper.writeValueAsString(event));
        outboxEvent.setStatus("PENDING");
        outboxEvent.setQueueName("stock.posted");
        outboxEventRepository.save(outboxEvent);
    }
}

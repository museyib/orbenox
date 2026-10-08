package com.orbenox.erp.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final JsonMapper jsonMapper;

    @Transactional
    public void createOutboxEvent(Object payload, String eventType, String aggregateType, String aggregateId, String routingKey) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(eventType);
        outboxEvent.setAggregateType(aggregateType);
        outboxEvent.setAggregateId(aggregateId);
        outboxEvent.setPayload(jsonMapper.writeValueAsString(payload));
        outboxEvent.setStatus("PENDING");
        outboxEvent.setRoutingKey(routingKey);
        outboxEventRepository.save(outboxEvent);
    }
}

package com.orbenox.erp.outbox;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

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
        outboxEvent.setCorrelationId(MDC.get("X-Correlation-Id"));
        outboxEventRepository.save(outboxEvent);
    }

    public List<OutboxEvent> getPendingEvents() {
        return outboxEventRepository.findAllByStatusOrderByCreatedAt("PENDING", Limit.of(5));
    }

    @Transactional
    public void updateStatus(long eventId, String status) {
        outboxEventRepository.updateStatus(eventId, status);
    }
}

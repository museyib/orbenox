package com.orbenox.erp.outbox;

import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.STOCK_POSTED_KEY;


@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final JsonMapper jsonMapper;

    public void createOutboxEvent(StockUpdatedEvent event, String eventType, String aggregateType, String correlationId) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(eventType);
        outboxEvent.setAggregateType(aggregateType);
        outboxEvent.setAggregateId(event.documentId().toString());
        outboxEvent.setPayload(jsonMapper.writeValueAsString(event));
        outboxEvent.setStatus("PENDING");
        outboxEvent.setRoutingKey(STOCK_POSTED_KEY);
        outboxEvent.setCorrelationId(correlationId);
        outboxEventRepository.save(outboxEvent);
    }

    @Transactional
    public List<OutboxEvent> getPendingEvents() {
        return outboxEventRepository.findAllByStatusOrderByCreatedAt("PENDING", Limit.of(5));
    }

    @Transactional
    public void updateStatus(long eventId, String status) {
        outboxEventRepository.updateStatus(eventId, status);
    }
}

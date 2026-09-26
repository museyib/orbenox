package com.orbenox.erp.outbox;

import com.orbenox.erp.transaction.entity.Document;
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
    public void createOutboxEvent(Document document, String eventType, String aggregateType) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(eventType);
        outboxEvent.setAggregateType(aggregateType);
        outboxEvent.setAggregateId(document.getDocumentNo());
        outboxEvent.setPayload(jsonMapper.writeValueAsString(document));
        outboxEvent.setStatus("PENDING");
        outboxEventRepository.save(outboxEvent);
    }
}

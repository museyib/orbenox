package com.orbenox.erp.outbox;

import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.projection.DocumentItem;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final DocumentRepository documentRepository;
    private final JsonMapper jsonMapper;

    @Transactional
    public void createOutboxEvent(Document document, String eventType, String aggregateType) {
        DocumentItem documentItem = documentRepository.getItemById(document.getId());
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(eventType);
        outboxEvent.setAggregateType(aggregateType);
        outboxEvent.setAggregateId(document.getId().toString());
        outboxEvent.setPayload(jsonMapper.writeValueAsString(documentItem));
        outboxEvent.setStatus("PENDING");
        outboxEventRepository.save(outboxEvent);
    }
}

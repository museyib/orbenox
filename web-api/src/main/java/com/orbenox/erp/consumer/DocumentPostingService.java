package com.orbenox.erp.consumer;

import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.message.EventMessage;
import com.orbenox.erp.messaging.command.StockMovementCommand.StockOperation;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.service.DocumentResolver;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
class DocumentPostingService {
    private final Counter eventConsumptionDuplicateCounter;
    private final DocumentResolver documentResolver;
    private final InboxEventRepository inboxEventRepository;
    private final JsonMapper jsonMapper;

    DocumentPostingService(DocumentResolver documentResolver,
                           InboxEventRepository inboxEventRepository,
                           JsonMapper jsonMapper,
                           MeterRegistry registry) {
        this.documentResolver = documentResolver;
        this.inboxEventRepository = inboxEventRepository;
        this.jsonMapper = jsonMapper;
        this.eventConsumptionDuplicateCounter = Counter.builder("event.consumption.duplicateAttempt").register(registry);
    }


    void handleStockUpdatedEvent(EventMessage eventMessage) {

        StockUpdatedEvent event = jsonMapper.readValue(eventMessage.payload(), StockUpdatedEvent.class);

        int affected = inboxEventRepository.createInboxEvent("web-api", eventMessage.eventId());

        if (affected > 0) {
            if (!event.success()) {
                documentResolver.resolveOptional(event.documentId(), event.typeCode())
                        .ifPresent(d -> d.setDocumentStatus(DocumentStatus.IN_PROGRESS));
                log.error("Stock posting failed: {}", event.message());
            } else {
                documentResolver.resolveOptional(event.documentId(), event.typeCode()).ifPresentOrElse(
                        doc -> {
                            doc.setDocumentStatus(DocumentStatus.POSTED);
                            updatePostedQuantities(doc, event.operations());
                            log.info("Document {} successfully posted", event.documentId());
                        },
                        () -> log.warn("Document {} not found for posting", event.documentId())
                );
            }
        } else {
            log.warn("Event {} already processed", eventMessage.eventId());
            eventConsumptionDuplicateCounter.increment();
        }

    }

    private void updatePostedQuantities(Document doc, List<StockOperation> operations) {
        Map<Long, BigDecimal> qtyMap = operations.stream()
                .collect(Collectors.toMap(StockOperation::lineId, StockOperation::quantity));

        for (var line : doc.getProductLines()) {
            BigDecimal opQty = qtyMap.get(line.getId());
            if (opQty != null) {
                line.setPostedQuantity(line.getPostedQuantity().add(opQty));
            }
        }
    }
}

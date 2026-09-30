package com.orbenox.erp.stockservice.consumer;

import com.orbenox.erp.stockservice.entity.Document;
import com.orbenox.erp.stockservice.repository.DocumentRepository;
import com.orbenox.erp.stockservice.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private static final String CONSUMER_NAME = "stock-service";

    private final InboxEventRepository inboxEventRepository;
    private final DocumentRepository documentRepository;
    private final StockService stockService;
    private final JsonMapper jsonMapper;

    @RabbitListener(queues = "stock-queue")
    @Transactional
    public void processEvent(String message) {
        try {
            EventMessage eventMessage = jsonMapper.readValue(message, EventMessage.class);
            int affected = inboxEventRepository.createInboxEvent(CONSUMER_NAME, eventMessage.eventId());
            if (affected > 0) {
                log.info("Message received: {}", message);
                Document document = documentRepository.findById(eventMessage.aggregateId()).orElseThrow();
                stockService.post(document);
            } else {
                log.warn("Event {} already processed", eventMessage.eventId());
            }
        } catch (JacksonException e) {
            log.error("Error processing message: {}", message, e);
            throw new RuntimeException(e);
        }
    }
}

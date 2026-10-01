package com.orbenox.erp.consumer;

import com.orbenox.erp.messaging.event.DocumentEvent;
import com.orbenox.erp.messaging.event.EventResponse;
import com.orbenox.erp.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final StockService stockService;

    @RabbitListener(queues = "stock-queue")
    public EventResponse processEvent(DocumentEvent eventMessage) {
        try {
            log.info("Message received: {}", eventMessage);
            stockService.post(eventMessage.id());
            return new EventResponse(true, "Stock processed successfully");
        } catch (Exception e) {
            log.error("Error processing message: {}", eventMessage, e);
            return new EventResponse(false, e.getMessage());
        }
    }
}

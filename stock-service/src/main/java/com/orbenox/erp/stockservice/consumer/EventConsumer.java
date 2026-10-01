package com.orbenox.erp.stockservice.consumer;

import com.orbenox.erp.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final StockService stockService;
    private final JsonMapper jsonMapper;

    @RabbitListener(queues = "stock-queue")
    public String processEvent(DocumentEvent eventMessage) {
        try {
            log.info("Message received: {}", eventMessage);
            stockService.post(eventMessage.id());
            return jsonMapper.writeValueAsString(new EventResponse(true, "OK"));
        } catch (Exception e) {
            log.error("Error processing message: {}", eventMessage, e);
            return jsonMapper.writeValueAsString(new EventResponse(false, e.getMessage()));
        }
    }
}

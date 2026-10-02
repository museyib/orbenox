package com.orbenox.erp.consumer;

import com.orbenox.erp.messaging.command.PostDocumentCommand;
import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final StockService stockService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "stock.post")
    public void processEvent(PostDocumentCommand command) {
        String queue = "stock.posted";
        try {
            log.info("Message received: {}", command);
            stockService.post(command.id());
            StockUpdatedEvent updatedEvent = new StockUpdatedEvent(false, command.id(), command.typeCode(), "Stock updated successfully");
            rabbitTemplate.convertAndSend(queue, updatedEvent);
            log.info("Message sent: {}", updatedEvent);
        } catch (Exception e) {
            log.error("Error processing message: {}", command, e);
            rabbitTemplate.convertAndSend(queue, new StockUpdatedEvent(false, command.id(), command.typeCode(), e.getMessage()));
        }
    }
}

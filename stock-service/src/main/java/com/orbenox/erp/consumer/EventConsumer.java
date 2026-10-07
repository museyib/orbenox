package com.orbenox.erp.consumer;

import com.orbenox.erp.config.RabbitMqConfiguration;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.service.StockPostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final StockPostingService stockService;

    @RabbitListener(queues = RabbitMqConfiguration.MAIN_QUEUE)
    public void processEvent(StockMovementCommand command) {
        log.info("Message received: {}", command);
        try {
            stockService.post(command);
        } catch (BusinessRuleException e) {
            stockService.publishFailureEvent(command, e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMqConfiguration.DLQ_QUEUE)
    public void processDeadLetter(String message) {
        log.error("Received bad message in DLQ: {}", message);
    }
}

package com.orbenox.erp.consumer;

import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.outbox.EventMessage;
import com.orbenox.erp.service.StockPostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final StockPostingService stockService;
    private final JsonMapper jsonMapper;

    @RabbitListener(queues = DOCUMENT_POSTED_QUEUE)
    public void processEvent(EventMessage eventMessage) {
        log.info("Message received: {}", eventMessage);
        StockMovementCommand command = jsonMapper.readValue(eventMessage.payload(), StockMovementCommand.class);
        try {
            stockService.post(command);
        } catch (BusinessRuleException e) {
            stockService.publishFailureEvent(command, e.getMessage());
        }
    }
}

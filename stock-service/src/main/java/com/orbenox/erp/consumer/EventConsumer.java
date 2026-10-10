package com.orbenox.erp.consumer;

import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.message.EventMessage;
import com.orbenox.erp.service.StockPostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final StockPostingService stockService;

    @RabbitListener(queues = DOCUMENT_POSTED_QUEUE)
    public void processEvent(EventMessage eventMessage) {
        log.info("Message received: {}", eventMessage);
        try {
            stockService.post(eventMessage);
        } catch (BusinessRuleException e) {
            stockService.publishFailureEvent(eventMessage, e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error processing event: {}", eventMessage, e);
            throw e;
        }
    }
}

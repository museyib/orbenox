package com.orbenox.erp.consumer;

import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.message.EventMessage;
import com.orbenox.erp.service.StockPostingService;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.*;

@Service
@Slf4j
public class EventConsumer {
    private final Counter eventConsumptionTotalCounter;
    private final Counter eventConsumptionSuccessCounter;
    private final Counter eventConsumptionDuplicateCounter;
    private final Counter eventConsumptionErrorCounter;
    private final StockPostingService stockService;

    public EventConsumer(StockPostingService stockService,
                         MeterRegistry registry) {
        this.stockService = stockService;
        this.eventConsumptionTotalCounter = Counter.builder("event.consumption.total").register(registry);
        this.eventConsumptionSuccessCounter = Counter.builder("event.consumption.success").register(registry);
        this.eventConsumptionDuplicateCounter = Counter.builder("event.consumption.duplicate").register(registry);
        this.eventConsumptionErrorCounter = Counter.builder("event.consumption.error").register(registry);
    }

    @RabbitListener(queues = DOCUMENT_POSTED_QUEUE)
    public void processEvent(EventMessage eventMessage) {
        try {
            eventConsumptionTotalCounter.increment();
            if (eventMessage.correlationId() != null) {
                MDC.put("X-Correlation-Id", eventMessage.correlationId());
            } else {
                MDC.remove("X-Correlation-Id");
            }
            log.info("Message received: {}", eventMessage);
            stockService.post(eventMessage);
            eventConsumptionSuccessCounter.increment();
        } catch (BusinessRuleException e) {
            try {
                stockService.publishFailureEvent(eventMessage, e.getMessage());
            } catch (RuntimeException publishException) {
                eventConsumptionErrorCounter.increment();
                log.error("Error publishing failure event for: {}", eventMessage, publishException);
                throw publishException;
            }
            eventConsumptionDuplicateCounter.increment();
        } catch (RuntimeException e) {
            log.error("Error processing event: {}", eventMessage, e);
            eventConsumptionErrorCounter.increment();
            throw e;
        } finally {
            MDC.remove("X-Correlation-Id");
        }
    }
}

package com.orbenox.erp.consumer;

import com.orbenox.erp.message.EventMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.STOCK_POSTED_QUEUE;

@Service
@Slf4j
public class EventConsumer {
    private final Counter eventConsumptionTotalCounter;
    private final Counter eventConsumptionSuccessCounter;
    private final Counter eventConsumptionErrorCounter;
    private final DocumentPostingService documentPostingService;

    public EventConsumer(DocumentPostingService documentPostingService,
                         MeterRegistry registry) {
        this.documentPostingService = documentPostingService;
        this.eventConsumptionTotalCounter = registry.counter("event.consumption.total");
        this.eventConsumptionSuccessCounter = registry.counter("event.consumption.success");
        this.eventConsumptionErrorCounter = registry.counter("event.consumption.error");
    }

    @RabbitListener(queues = STOCK_POSTED_QUEUE)
    public void processEvent(EventMessage eventMessage) {
        try {
            eventConsumptionTotalCounter.increment();
            if (eventMessage.correlationId() != null) {
                MDC.put("X-Correlation-Id", eventMessage.correlationId());
            } else {
                MDC.remove("X-Correlation-Id");
            }
            log.info("Message received: {}", eventMessage);
            documentPostingService.handleStockUpdatedEvent(eventMessage);
            eventConsumptionSuccessCounter.increment();
        } catch (Exception e) {
            eventConsumptionErrorCounter.increment();
            log.error("Error processing event: {}", eventMessage, e);
        } finally {
            MDC.remove("X-Correlation-Id");
        }
    }
}

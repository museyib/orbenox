package com.orbenox.erp.consumer;

import com.orbenox.erp.message.EventMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.NOTIFICATION_QUEUE;

@Service
@Slf4j
public class EventConsumer {

    private final Counter eventConsumptionTotalCounter;
    private final Counter eventConsumptionSuccessCounter;
    private final Counter eventConsumptionErrorCounter;
    private final InboxEventRepository inboxEventRepository;

    public EventConsumer(InboxEventRepository inboxEventRepository,
                         MeterRegistry registry) {
        this.inboxEventRepository = inboxEventRepository;
        this.eventConsumptionTotalCounter = registry.counter("event.consumption.total");
        this.eventConsumptionSuccessCounter = registry.counter("event.consumption.success");
        this.eventConsumptionErrorCounter = registry.counter("event.consumption.error");
    }

    @RabbitListener(queues = NOTIFICATION_QUEUE)
    @Transactional
    public void processEvent(EventMessage eventMessage) {
        try {
            eventConsumptionTotalCounter.increment();
            if (eventMessage.correlationId() != null) {
                MDC.put("X-Correlation-Id", eventMessage.correlationId());
            } else {
                MDC.remove("X-Correlation-Id");
            }
            log.info("Message received: {}", eventMessage);
            int affected = inboxEventRepository.createInboxEvent("notification-service", eventMessage.eventId());
            if (affected > 0) {
                eventConsumptionSuccessCounter.increment();
                log.info("Event {} processed", eventMessage.eventId());
            } else {
                eventConsumptionErrorCounter.increment();
                log.warn("Event {} already processed", eventMessage.eventId());
            }
        } catch (JacksonException e) {
            eventConsumptionErrorCounter.increment();
            log.error("Error processing message: {}", eventMessage, e);
            throw new RuntimeException(e);
        } finally {
            MDC.remove("X-Correlation-Id");
        }
    }
}

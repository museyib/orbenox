package com.orbenox.erp.outbox;

import com.orbenox.erp.message.EventMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.*;

@Service
@Slf4j
public class OutboxPublisher {

    private final Counter outboxPublishSuccess;
    private final Counter outboxPublishError;

    private final OutboxEventService outboxEventService;
    private final RabbitTemplate rabbitTemplate;

    public OutboxPublisher(OutboxEventService outboxEventService,
                           RabbitTemplate rabbitTemplate,
                           MeterRegistry meterRegistry) {
        this.outboxEventService = outboxEventService;
        this.rabbitTemplate = rabbitTemplate;
        this.outboxPublishSuccess = meterRegistry.counter("outbox.publish.success");
        this.outboxPublishError = meterRegistry.counter("outbox.publish.error");
    }

    @Transactional
    @Scheduled(fixedDelay = 5000)
    @SchedulerLock(name = "outbox_publisher_lock", lockAtMostFor = "60s", lockAtLeastFor = "2s")
    public void publishEvents() {
        List<OutboxEvent> pending = outboxEventService.getPendingEvents();

        for (OutboxEvent event : pending) {
            EventMessage eventMessage = new EventMessage(
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateType(),
                    event.getAggregateId(),
                    event.getAggregateVersion(),
                    event.getPayload(),
                    event.getCorrelationId());

            try {
                String correlationId = event.getCorrelationId();
                if (correlationId != null) {
                    MDC.put("X-Correlation-Id", correlationId);
                } else {
                    MDC.remove("X-Correlation-Id");
                }

                CorrelationData correlationData = new CorrelationData(event.getId().toString());
                rabbitTemplate.convertAndSend(ERP_EXCHANGE, event.getRoutingKey(), eventMessage, correlationData);

                CorrelationData.Confirm confirm = correlationData.getFuture().get(5, TimeUnit.SECONDS);

                if (!confirm.ack()) {
                    log.error("Event with id {} failed to publish", event.getId());
                    outboxPublishError.increment();
                    continue;
                }

                if (correlationData.getReturned() != null) {
                    var returned = correlationData.getReturned();

                    log.error("Event {} was returned {}", event.getId(), returned.getReplyText());
                    outboxPublishError.increment();
                    continue;
                }

                outboxPublishSuccess.increment();
                outboxEventService.updateStatus(event.getId(), "PUBLISHED");

                log.info("Event with id {} published", event.getId());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while publishing event with id {}", event.getId());
                break;
            } catch (AmqpException | ExecutionException | TimeoutException e) {
                log.error("Error publishing event with id {}", event.getId(), e);
                outboxPublishError.increment();
            } finally {
                MDC.remove("X-Correlation-Id");
            }
        }
    }
}

package com.orbenox.erp.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Limit;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.orbenox.erp.config.RabbitMqInfrastructureConfiguration.*;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    @SchedulerLock(name = "outbox_publisher_lock", lockAtMostFor = "30s", lockAtLeastFor = "2s")
    public void publishEvents() {
        List<OutboxEvent> pending = outboxEventRepository.findAllByStatusOrderByCreatedAt("PENDING", Limit.of(10));

        for (OutboxEvent event : pending) {
            EventMessage eventMessage = new EventMessage(
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateType(),
                    event.getAggregateId(),
                    event.getAggregateVersion(),
                    event.getPayload());

            try {
                CorrelationData correlationData = new CorrelationData(event.getId().toString());
                rabbitTemplate.convertAndSend(ERP_EXCHANGE, event.getRoutingKey(), eventMessage, correlationData);

                CorrelationData.Confirm confirm = correlationData.getFuture().get(5, TimeUnit.SECONDS);

                if (!confirm.ack()) {
                    log.error("Event with id {} failed to publish", event.getId());
                    continue;
                }

                if (correlationData.getReturned() != null) {
                    var returned = correlationData.getReturned();

                    log.error("Event{} was returned {}", event.getId(), returned.getReplyText());
                    continue;
                }

                outboxEventRepository.updateStatus(event.getId(), "PUBLISHED");

                log.info("Event with id {} published", event.getId());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while publishing event with id {}", event.getId());
                return;
            } catch (AmqpException | ExecutionException | TimeoutException e) {
                log.error("Error publishing event with id {}", event.getId(), e);
            }
        }
    }
}

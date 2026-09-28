package com.orbenox.erp.eventpublisher.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Limit;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {
        List<OutboxEvent> pending = outboxEventRepository.findAllByStatusOrderByCreatedAt("PENDING", Limit.of(10));

        pending.forEach(event -> {
            EventMessage eventMessage = new EventMessage(
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateType(),
                    event.getAggregateId(),
                    event.getAggregateVersion(),
                    event.getPayload());
            String queueName = "";
            if (event.getEventType().endsWith("POSTED"))
                queueName = "stock-queue";
            else if (event.getEventType().endsWith("CREATED"))
                queueName = "notification-queue";
            rabbitTemplate.convertAndSend(queueName, eventMessage);
            outboxEventRepository.updateStatus(event.getId(), "PUBLISHED");
            log.info("Event with id {} published", event.getId());
        });
    }
}

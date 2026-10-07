package com.orbenox.erp.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Limit;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.hibernate.internal.util.StringHelper.isEmpty;

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

            rabbitTemplate.convertAndSend("notification.queue", eventMessage);
            if (!isEmpty(event.getQueueName()))
                rabbitTemplate.convertAndSend(event.getQueueName(), eventMessage);
            log.info("Event with id {} published", event.getId());
            outboxEventRepository.updateStatus(event.getId(), "PUBLISHED");
        });
    }
}

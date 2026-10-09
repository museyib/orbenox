package com.orbenox.erp.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Limit;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        pending.forEach(event -> {
            EventMessage eventMessage = new EventMessage(
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateType(),
                    event.getAggregateId(),
                    event.getAggregateVersion(),
                    event.getPayload());
            rabbitTemplate.convertAndSend(ERP_EXCHANGE, event.getRoutingKey(), eventMessage);
            log.info("Event with id {} published", event.getId());
            outboxEventRepository.updateStatus(event.getId(), "PUBLISHED");
        });
    }
}

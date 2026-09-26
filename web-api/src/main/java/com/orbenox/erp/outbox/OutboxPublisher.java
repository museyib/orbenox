package com.orbenox.erp.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
        List<OutboxEvent> pending = outboxEventRepository.findAllByStatus("PENDING");

        pending.forEach(event -> {
            rabbitTemplate.convertAndSend("outbox-exchange", "outbox-routing-key", event);
            outboxEventRepository.updateStatus(event.getId(), "PUBLISHED");
            log.info("Event with id {} published", event.getId());
        });
    }
}

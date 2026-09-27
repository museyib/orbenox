package com.orbenox.erp.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final InboxEventRepository inboxEventRepository;

    @RabbitListener(queues = "outbox-queue")
    public void processEvent(String message) {
        Random random = new Random();
        Long randomNumber = random.nextLong(100);
        inboxEventRepository.createInboxEvent("event_consumer", randomNumber);
        log.info("Message received: {}", message);
    }
}

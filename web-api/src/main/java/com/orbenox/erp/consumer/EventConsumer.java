package com.orbenox.erp.consumer;

import com.orbenox.erp.outbox.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final DocumentPostingService documentPostingService;

    @RabbitListener(queues = "stock.posted")
    public void processEvent(EventMessage eventMessage) {

        log.info("Message received: {}", eventMessage);

        documentPostingService.handleStockUpdatedEvent(eventMessage);
    }
}

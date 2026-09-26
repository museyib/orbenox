package com.orbenox.erp.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public void save(OutboxEvent outboxEvent) {
        outboxEventRepository.save(outboxEvent);
    }
}

package com.orbenox.erp.config;

import com.orbenox.erp.outbox.OutboxEventRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {

    @Bean
    public Gauge oldestPendingOutboxEvent(OutboxEventRepository outboxEventRepository, MeterRegistry meterRegistry) {
        return Gauge.builder(
                        "outbox.pending.oldest.age",
                        outboxEventRepository,
                        OutboxEventRepository::findOldestPendingAgeSeconds)
                .register(meterRegistry);
    }

    @Bean
    public Gauge pendingOutboxEventCount(OutboxEventRepository outboxEventRepository, MeterRegistry meterRegistry) {
        return Gauge.builder(
                        "outbox.pending.count",
                        outboxEventRepository,
                        outboxEventRepository1 -> outboxEventRepository1.countByStatus("PENDING"))
                .register(meterRegistry);
    }
}

package com.orbenox.erp.service;

import com.orbenox.erp.consumer.InboxEventRepository;
import com.orbenox.erp.message.EventMessage;
import com.orbenox.erp.outbox.OutboxEventService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockPostingServiceTest {

    @Mock
    private StockService stockService;

    @Mock
    private InboxEventRepository inboxEventRepository;

    @Mock
    private OutboxEventService outboxEventService;

    @Mock
    private JsonMapper jsonMapper;

    private MeterRegistry meterRegistry;
    private StockPostingService stockPostingService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        stockPostingService = new StockPostingService(
                stockService, inboxEventRepository, outboxEventService, jsonMapper, meterRegistry);
    }

    @Test
    void post_whenEventWasAlreadyProcessed_shouldIncrementDuplicateAttemptCounter() {
        EventMessage eventMessage = new EventMessage(
                301L, "STOCK_POSTED", "SALES_ORDER", "12", "1", "{}", "correlation-stock-duplicate");
        when(inboxEventRepository.createInboxEvent("stock-service", 301L)).thenReturn(0);

        stockPostingService.post(eventMessage);

        verify(inboxEventRepository).createInboxEvent("stock-service", 301L);
        assertThat(meterRegistry.get("event.consumption.duplicateAttempt").counter().count()).isEqualTo(1);
    }
}

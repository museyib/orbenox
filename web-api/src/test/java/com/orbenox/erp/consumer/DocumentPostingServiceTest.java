package com.orbenox.erp.consumer;

import com.orbenox.erp.messaging.event.StockUpdatedEvent;
import com.orbenox.erp.message.EventMessage;
import com.orbenox.erp.transaction.service.DocumentResolver;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentPostingServiceTest {

    @Mock
    private DocumentResolver documentResolver;

    @Mock
    private InboxEventRepository inboxEventRepository;

    @Mock
    private JsonMapper jsonMapper;

    private MeterRegistry meterRegistry;
    private DocumentPostingService documentPostingService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        documentPostingService = new DocumentPostingService(
                documentResolver, inboxEventRepository, jsonMapper, meterRegistry);
    }

    @Test
    void handleStockUpdatedEvent_whenEventWasAlreadyProcessed_shouldIncrementDuplicateAttemptCounter() {
        EventMessage eventMessage = new EventMessage(
                302L, "STOCK_POSTED", "SALES_ORDER", "12", "1", "{}", "correlation-web-duplicate");
        when(jsonMapper.readValue("{}", StockUpdatedEvent.class))
                .thenReturn(new StockUpdatedEvent(true, 12L, "SALES_ORDER", "", List.of()));
        when(inboxEventRepository.createInboxEvent("web-api", 302L)).thenReturn(0);

        documentPostingService.handleStockUpdatedEvent(eventMessage);

        verify(inboxEventRepository).createInboxEvent("web-api", 302L);
        assertThat(meterRegistry.get("event.consumption.duplicateAttempt").counter().count()).isEqualTo(1);
    }
}

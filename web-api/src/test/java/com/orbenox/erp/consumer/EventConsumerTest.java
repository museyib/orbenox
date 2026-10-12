package com.orbenox.erp.consumer;

import com.orbenox.erp.message.EventMessage;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

    @Mock
    private DocumentPostingService documentPostingService;

    private EventConsumer eventConsumer;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        eventConsumer = new EventConsumer(documentPostingService, meterRegistry);
    }

    @Test
    void processEvent_whenStockPostingSucceeded_shouldDelegateEvent() {
        EventMessage eventMessage = eventMessage(
                101L, "{\"success\":true,\"documentId\":12,\"typeCode\":\"SALES_ORDER\"}");
        doAnswer(invocation -> {
            assertThat(MDC.get("X-Correlation-Id")).isEqualTo(eventMessage.correlationId());
            return null;
        }).when(documentPostingService).handleStockUpdatedEvent(eventMessage);

        eventConsumer.processEvent(eventMessage);

        verify(documentPostingService).handleStockUpdatedEvent(eventMessage);
        assertThat(MDC.get("X-Correlation-Id")).isNull();
        assertCounters(1, 1, 0);
    }

    @Test
    void processEvent_whenStockPostingFailed_shouldDelegateEvent() {
        EventMessage eventMessage = eventMessage(
                102L, "{\"success\":false,\"documentId\":13,\"typeCode\":\"PRODUCT_APPROVE\"}");

        eventConsumer.processEvent(eventMessage);

        verify(documentPostingService).handleStockUpdatedEvent(eventMessage);
    }

    @Test
    void processEvent_whenTechnicalFailureOccurs_shouldIncrementErrorCounterAndPropagate() {
        EventMessage eventMessage = eventMessage(
                103L, "{\"success\":true,\"documentId\":14,\"typeCode\":\"SALES_ORDER\"}");
        doThrow(new IllegalStateException("database unavailable"))
                .when(documentPostingService).handleStockUpdatedEvent(eventMessage);

        assertThatThrownBy(() -> eventConsumer.processEvent(eventMessage))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("database unavailable");

        assertCounters(1, 0, 1);
    }

    private void assertCounters(double total, double success, double error) {
        assertThat(meterRegistry.get("event.consumption.total").counter().count()).isEqualTo(total);
        assertThat(meterRegistry.get("event.consumption.success").counter().count()).isEqualTo(success);
        assertThat(meterRegistry.get("event.consumption.error").counter().count()).isEqualTo(error);
    }

    private EventMessage eventMessage(Long eventId, String payload) {
        return new EventMessage(
                eventId,
                "DOCUMENT_POSTED",
                "DOCUMENT",
                "12",
                "1",
                payload,
                "correlation-web-test");
    }
}

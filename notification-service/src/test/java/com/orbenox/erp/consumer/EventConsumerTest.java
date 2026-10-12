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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

    @Mock
    private InboxEventRepository inboxEventRepository;

    private EventConsumer eventConsumer;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        eventConsumer = new EventConsumer(inboxEventRepository, meterRegistry);
    }

    @Test
    void processEvent_whenNewMessageArrives_shouldRecordNotificationEvent() {
        EventMessage message = new EventMessage(201L, "SALES_ORDER_CREATED",
                "SALES_ORDER", "12", "1", "{}", "correlation-notification-201");
        when(inboxEventRepository.createInboxEvent("notification-service", 201L)).thenAnswer(invocation -> {
            assertThat(MDC.get("X-Correlation-Id")).isEqualTo(message.correlationId());
            return 1;
        });

        eventConsumer.processEvent(message);

        verify(inboxEventRepository).createInboxEvent("notification-service", 201L);
        assertThat(MDC.get("X-Correlation-Id")).isNull();
        assertCounters(1, 1, 0, 0);
    }

    @Test
    void processEvent_whenMessageWasAlreadyProcessed_shouldNotInsertItAgain() {
        EventMessage message = new EventMessage(202L, "PRODUCT_APPROVE_CREATED",
                "PRODUCT_APPROVE", "13", "1", "{}", "correlation-notification-202");
        when(inboxEventRepository.createInboxEvent("notification-service", 202L)).thenReturn(0);

        eventConsumer.processEvent(message);

        verify(inboxEventRepository).createInboxEvent("notification-service", 202L);
        assertCounters(1, 1, 1, 0);
    }

    private void assertCounters(double total, double success, double duplicate, double error) {
        assertThat(meterRegistry.get("event.consumption.total").counter().count()).isEqualTo(total);
        assertThat(meterRegistry.get("event.consumption.success").counter().count()).isEqualTo(success);
        assertThat(meterRegistry.get("event.consumption.duplicateAttempt").counter().count()).isEqualTo(duplicate);
        assertThat(meterRegistry.get("event.consumption.error").counter().count()).isEqualTo(error);
    }
}

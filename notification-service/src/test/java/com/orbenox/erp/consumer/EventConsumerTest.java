package com.orbenox.erp.consumer;

import com.orbenox.erp.message.EventMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

    @Mock
    private InboxEventRepository inboxEventRepository;

    @Mock
    private JsonMapper jsonMapper;

    @InjectMocks
    private EventConsumer eventConsumer;

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
    }

    @Test
    void processEvent_whenMessageWasAlreadyProcessed_shouldNotInsertItAgain() {
        EventMessage message = new EventMessage(202L, "PRODUCT_APPROVE_CREATED",
                "PRODUCT_APPROVE", "13", "1", "{}", "correlation-notification-202");
        when(inboxEventRepository.createInboxEvent("notification-service", 202L)).thenReturn(0);

        eventConsumer.processEvent(message);

        verify(inboxEventRepository).createInboxEvent("notification-service", 202L);
    }
}

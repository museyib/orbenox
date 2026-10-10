package com.orbenox.erp.consumer;

import com.orbenox.erp.message.EventMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.json.JsonMapper;

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
                "SALES_ORDER", "12", "1", "{}");
        when(inboxEventRepository.createInboxEvent("notification-service", 201L)).thenReturn(1);

        eventConsumer.processEvent(message);

        verify(inboxEventRepository).createInboxEvent("notification-service", 201L);
    }

    @Test
    void processEvent_whenMessageWasAlreadyProcessed_shouldNotInsertItAgain() {
        EventMessage message = new EventMessage(202L, "PRODUCT_APPROVE_CREATED",
                "PRODUCT_APPROVE", "13", "1", "{}");
        when(inboxEventRepository.createInboxEvent("notification-service", 202L)).thenReturn(0);

        eventConsumer.processEvent(message);

        verify(inboxEventRepository).createInboxEvent("notification-service", 202L);
    }
}

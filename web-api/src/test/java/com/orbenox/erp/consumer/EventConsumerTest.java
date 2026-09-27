package com.orbenox.erp.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbenox.erp.outbox.EventMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

    @Mock
    private InboxEventRepository inboxEventRepository;

    private EventConsumer eventConsumer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        eventConsumer = new EventConsumer(inboxEventRepository, objectMapper);
    }

    @Test
    void processEvent_shouldRecordEventIdForValidMessage() throws Exception {
        String message = objectMapper.writeValueAsString(new EventMessage(
                42L, "ORDER_CREATED", "Order", "order-1", "1", "{}", "{}"));
        when(inboxEventRepository.createInboxEvent("event_consumer", 42L)).thenReturn(1);

        eventConsumer.processEvent(message);

        verify(inboxEventRepository).createInboxEvent("event_consumer", 42L);
    }

    @Test
    void processEvent_shouldIgnoreDuplicateEventWhenAlreadyRecorded() throws Exception {
        String message = objectMapper.writeValueAsString(new EventMessage(
                42L, "ORDER_CREATED", "Order", "order-1", "1", "{}", "{}"));
        when(inboxEventRepository.createInboxEvent("event_consumer", 42L)).thenReturn(0);

        eventConsumer.processEvent(message);

        verify(inboxEventRepository).createInboxEvent("event_consumer", 42L);
    }

    @Test
    void processEvent_shouldWrapJsonProcessingExceptionWhenMessageIsMalformed() {
        assertThatThrownBy(() -> eventConsumer.processEvent("{"))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(JsonProcessingException.class);

        verifyNoInteractions(inboxEventRepository);
    }
}

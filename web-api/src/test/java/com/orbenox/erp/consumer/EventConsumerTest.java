package com.orbenox.erp.consumer;

import com.orbenox.erp.outbox.EventMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

    @Mock
    private DocumentPostingService documentPostingService;

    @InjectMocks
    private EventConsumer eventConsumer;

    @Test
    void processEvent_whenStockPostingSucceeded_shouldDelegateEvent() {
        EventMessage eventMessage = eventMessage(
                101L, "{\"success\":true,\"documentId\":12,\"typeCode\":\"SALES_ORDER\"}");

        eventConsumer.processEvent(eventMessage);

        verify(documentPostingService).handleStockUpdatedEvent(eventMessage);
    }

    @Test
    void processEvent_whenStockPostingFailed_shouldDelegateEvent() {
        EventMessage eventMessage = eventMessage(
                102L, "{\"success\":false,\"documentId\":13,\"typeCode\":\"PRODUCT_APPROVE\"}");

        eventConsumer.processEvent(eventMessage);

        verify(documentPostingService).handleStockUpdatedEvent(eventMessage);
    }

    private EventMessage eventMessage(Long eventId, String payload) {
        return new EventMessage(
                eventId,
                "DOCUMENT_POSTED",
                "DOCUMENT",
                "12",
                "1",
                payload);
    }
}

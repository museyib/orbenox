package com.orbenox.erp.consumer;

import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.outbox.EventMessage;
import com.orbenox.erp.service.StockPostingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

    @Mock
    private StockPostingService stockPostingService;

    @Mock
    private JsonMapper jsonMapper;

    @InjectMocks
    private EventConsumer eventConsumer;

    @Test
    void processEvent_whenStockUpdateSucceeds_shouldNotPublishFailureEvent() {
        StockMovementCommand command = command();

        EventMessage eventMessage = new EventMessage(
                1L,
                "STOCK_POSTED",
                "PRODUCT_APPROVE",
                "12",
                "",
                new JsonMapper().writeValueAsString(command)
        );

        eventConsumer.processEvent(eventMessage);

        verify(stockPostingService).post(eventMessage);
        verify(stockPostingService, never()).publishFailureEvent(any(), anyString());
    }

    @Test
    void processEvent_whenStockUpdateViolatesBusinessRule_shouldPublishFailureEvent() {
        StockMovementCommand command = command();

        EventMessage eventMessage = new EventMessage(
                1L,
                "STOCK_POSTED",
                "PRODUCT_APPROVE",
                "12",
                "",
                new JsonMapper().writeValueAsString(command)
        );
        whenStockUpdateFails(eventMessage, new BusinessRuleException("Insufficient stock"));

        assertThrows(BusinessRuleException.class, () -> eventConsumer.processEvent(eventMessage));
    }

    @Test
    void processEvent_whenTechnicalFailureOccurs_shouldPropagateForMessageRedelivery() {
        StockMovementCommand command = command();
        RuntimeException failure = new IllegalStateException("Database unavailable");

        EventMessage eventMessage = new EventMessage(
                1L,
                "STOCK_POSTED",
                "PRODUCT_APPROVE",
                "12",
                "",
                new JsonMapper().writeValueAsString(command)
        );
        whenStockUpdateFails(eventMessage, failure);

        assertThatThrownBy(() -> eventConsumer.processEvent(eventMessage))
                .isSameAs(failure);

        verify(stockPostingService, never()).publishFailureEvent(any(), anyString());
    }

    private void whenStockUpdateFails(EventMessage eventMessage, RuntimeException failure) {
        doThrow(failure).when(stockPostingService).post(eventMessage);
    }

    private StockMovementCommand command() {
        return new StockMovementCommand(12L, "SO-12", List.of(
                new StockMovementCommand.StockOperation(11L, 1L, 1L, BigDecimal.TEN, 1),
                new StockMovementCommand.StockOperation(12L, 2L, 1L, BigDecimal.TEN, 1)
        ));
    }
}

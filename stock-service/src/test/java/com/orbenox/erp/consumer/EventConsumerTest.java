package com.orbenox.erp.consumer;

import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.service.StockPostingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

    @Mock
    private StockPostingService stockPostingService;

    @InjectMocks
    private EventConsumer eventConsumer;

    @Test
    void processEvent_whenStockUpdateSucceeds_shouldNotPublishFailureEvent() {
        StockMovementCommand command = command();

        eventConsumer.processEvent(command);

        verify(stockPostingService).post(command);
        verify(stockPostingService, never()).publishFailureEvent(any(), anyString());
    }

    @Test
    void processEvent_whenStockUpdateViolatesBusinessRule_shouldPublishFailureEvent() {
        StockMovementCommand command = command();
        whenStockUpdateFails(command, new BusinessRuleException("Insufficient stock"));

        eventConsumer.processEvent(command);

        verify(stockPostingService).publishFailureEvent(command, "Insufficient stock");
    }

    @Test
    void processEvent_whenTechnicalFailureOccurs_shouldPropagateForMessageRedelivery() {
        StockMovementCommand command = command();
        RuntimeException failure = new IllegalStateException("Database unavailable");
        whenStockUpdateFails(command, failure);

        assertThatThrownBy(() -> eventConsumer.processEvent(command))
                .isSameAs(failure);

        verify(stockPostingService, never()).publishFailureEvent(any(), anyString());
    }

    @Test
    void processDeadLetter_shouldConsumeMessageWithoutReprocessingStockCommand() {
        eventConsumer.processDeadLetter("dead-lettered malformed command");

        verifyNoInteractions(stockPostingService);
    }

    private void whenStockUpdateFails(StockMovementCommand command, RuntimeException failure) {
        doThrow(failure).when(stockPostingService).post(command);
    }

    private StockMovementCommand command() {
        return new StockMovementCommand(12L, "SO-12", List.of(
                new StockMovementCommand.StockOperation(11L, 1L, 1L, BigDecimal.TEN, 1),
                new StockMovementCommand.StockOperation(12L, 2L, 1L, BigDecimal.TEN, 1)
        ));
    }
}

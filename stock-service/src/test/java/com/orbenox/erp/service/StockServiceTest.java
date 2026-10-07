package com.orbenox.erp.service;

import com.orbenox.erp.entity.StockMovement;
import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.messaging.command.StockMovementCommand.StockOperation;
import com.orbenox.erp.repository.StockBalanceRepository;
import com.orbenox.erp.repository.StockMovementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private StockBalanceRepository stockBalanceRepository;

    @InjectMocks
    private StockService stockService;

    @Test
    void updateStock_whenCommandHasInboundOperation_shouldIncreaseStockAndRecordMovement() {
        StockMovementCommand command = command(new StockOperation(101L, 34L, 56L, BigDecimal.valueOf(5), 1));
        when(stockBalanceRepository.increaseQuantity(34L, 56L, BigDecimal.valueOf(5))).thenReturn(1);

        stockService.updateStock(command);

        ArgumentCaptor<StockMovement> movementCaptor = ArgumentCaptor.forClass(StockMovement.class);
        verify(stockMovementRepository).save(movementCaptor.capture());
        verify(stockBalanceRepository).increaseQuantity(34L, 56L, BigDecimal.valueOf(5));
        assertThat(movementCaptor.getValue().getDocumentId()).isEqualTo(12L);
        assertThat(movementCaptor.getValue().getProductId()).isEqualTo(34L);
        assertThat(movementCaptor.getValue().getWarehouseId()).isEqualTo(56L);
        assertThat(movementCaptor.getValue().getQuantity()).isEqualByComparingTo("5");
        verify(stockBalanceRepository, never()).decreaseQuantity(anyLong(), anyLong(), any());
    }

    @Test
    void updateStock_whenCommandHasOutboundOperation_shouldDecreaseStockAndRecordNegativeMovement() {
        StockMovementCommand command = command(new StockOperation(101L, 34L, 56L, BigDecimal.valueOf(5), -1));
        when(stockBalanceRepository.decreaseQuantity(34L, 56L, BigDecimal.valueOf(5))).thenReturn(1);

        stockService.updateStock(command);

        ArgumentCaptor<StockMovement> movementCaptor = ArgumentCaptor.forClass(StockMovement.class);
        verify(stockMovementRepository).save(movementCaptor.capture());
        verify(stockBalanceRepository).decreaseQuantity(34L, 56L, BigDecimal.valueOf(5));
        assertThat(movementCaptor.getValue().getDocumentId()).isEqualTo(12L);
        assertThat(movementCaptor.getValue().getQuantity()).isEqualByComparingTo("-5");
        verify(stockBalanceRepository, never()).increaseQuantity(anyLong(), anyLong(), any());
    }

    @Test
    void updateStock_whenOperationsAreUnsorted_shouldCreateMovementsInDeterministicOrder() {
        StockMovementCommand command = command(
                new StockOperation(103L, 35L, 56L, BigDecimal.valueOf(3), 1),
                new StockOperation(102L, 34L, 56L, BigDecimal.valueOf(5), 1),
                new StockOperation(101L, 34L, 56L, BigDecimal.valueOf(2), -1),
                new StockOperation(104L, 34L, 55L, BigDecimal.ONE, 1));
        when(stockBalanceRepository.increaseQuantity(anyLong(), anyLong(), any())).thenReturn(1);
        when(stockBalanceRepository.decreaseQuantity(anyLong(), anyLong(), any())).thenReturn(1);

        stockService.updateStock(command);

        ArgumentCaptor<StockMovement> movementCaptor = ArgumentCaptor.forClass(StockMovement.class);
        verify(stockMovementRepository, times(4)).save(movementCaptor.capture());
        assertThat(movementCaptor.getAllValues())
                .extracting(StockMovement::getWarehouseId, StockMovement::getProductId, StockMovement::getQuantity)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(55L, 34L, BigDecimal.ONE),
                        org.assertj.core.groups.Tuple.tuple(56L, 34L, BigDecimal.valueOf(-2)),
                        org.assertj.core.groups.Tuple.tuple(56L, 34L, BigDecimal.valueOf(5)),
                        org.assertj.core.groups.Tuple.tuple(56L, 35L, BigDecimal.valueOf(3)));
    }

    @Test
    void updateStock_whenOutboundOperationHasInsufficientBalance_shouldReportBusinessFailure() {
        StockMovementCommand command = command(new StockOperation(101L, 34L, 56L, BigDecimal.valueOf(5), -1));
        when(stockBalanceRepository.decreaseQuantity(34L, 56L, BigDecimal.valueOf(5))).thenReturn(0);

        assertThatThrownBy(() -> stockService.updateStock(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Insufficient stock quantity");

        verify(stockMovementRepository).save(any(StockMovement.class));
        verify(stockBalanceRepository, never()).increaseQuantity(anyLong(), anyLong(), any());
    }

    @Test
    void updateStock_whenInboundOperationCannotUpdateBalance_shouldReportBusinessFailure() {
        StockMovementCommand command = command(new StockOperation(101L, 34L, 56L, BigDecimal.valueOf(5), 1));
        when(stockBalanceRepository.increaseQuantity(34L, 56L, BigDecimal.valueOf(5))).thenReturn(0);

        assertThatThrownBy(() -> stockService.updateStock(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Something went wrong for the product: 34");

        verify(stockMovementRepository).save(any(StockMovement.class));
        verify(stockBalanceRepository, never()).decreaseQuantity(anyLong(), anyLong(), any());
    }

    @Test
    void updateStock_whenCommandHasNoOperations_shouldNotWriteMovementsOrBalances() {
        stockService.updateStock(new StockMovementCommand(12L, "SALES_ORDER", new ArrayList<>()));

        verifyNoInteractions(stockMovementRepository, stockBalanceRepository);
    }

    private StockMovementCommand command(StockOperation... operations) {
        return new StockMovementCommand(12L, "SALES_ORDER", new ArrayList<>(List.of(operations)));
    }
}

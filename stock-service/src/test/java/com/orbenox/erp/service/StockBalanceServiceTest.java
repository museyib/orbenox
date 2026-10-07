package com.orbenox.erp.service;

import com.orbenox.erp.projection.StockBalanceItem;
import com.orbenox.erp.repository.StockBalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockBalanceServiceTest {

    @Mock
    private StockBalanceRepository stockBalanceRepository;

    @InjectMocks
    private StockBalanceService stockBalanceService;

    @Test
    void getItemsByProductId_shouldReturnRepositoryResults() {
        List<StockBalanceItem> items = List.of();
        when(stockBalanceRepository.getStockBalanceItems(34L)).thenReturn(items);

        assertThat(stockBalanceService.getItemsByProductId(34L)).isSameAs(items);
        verify(stockBalanceRepository).getStockBalanceItems(34L);
    }
}

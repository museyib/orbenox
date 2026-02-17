package com.orbenox.erp.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockBalanceService {
    private final StockBalanceRepository stockBalanceRepo;

    public List<StockBalanceItem> getItemsByProductId(Long productId) {
        return stockBalanceRepo.getItemsByProductId(productId);
    }
}

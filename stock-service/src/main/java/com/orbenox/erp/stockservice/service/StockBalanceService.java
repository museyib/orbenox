package com.orbenox.erp.stockservice.service;

import com.orbenox.erp.stockservice.projection.StockBalanceItem;
import com.orbenox.erp.stockservice.repository.StockBalanceRepository;
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

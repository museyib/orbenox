package com.orbenox.erp.service;

import com.orbenox.erp.projection.StockBalanceItem;
import com.orbenox.erp.repository.StockBalanceRepository;
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

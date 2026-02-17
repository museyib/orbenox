package com.orbenox.erp.domain.stock;

import com.orbenox.erp.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stockBalance")
public class StockBalanceController {
    private final StockBalanceService stockBalanceService;

    @GetMapping("/{productId}")
    public ResponseEntity<Response<List<StockBalanceItem>>> getAllStockBalanceItems(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.successData(stockBalanceService.getItemsByProductId(productId)));
    }
}

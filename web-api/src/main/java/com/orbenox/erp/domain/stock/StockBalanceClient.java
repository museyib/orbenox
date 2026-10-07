package com.orbenox.erp.domain.stock;

import com.orbenox.erp.common.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "stock-service")
public interface StockBalanceClient {

    @GetMapping("/api/stockBalance/{productId}")
    Response<List<SimpleStockBalanceItem>> getStockBalanceItems(@PathVariable("productId") Long productId);

    @GetMapping("/api/stockBalance/{productId}/{warehouseId}")
    Response<SimpleStockBalanceItem> getStockBalanceItem(@PathVariable("productId") Long productId,
                                                   @PathVariable("warehouseId") Long warehouseId);
}

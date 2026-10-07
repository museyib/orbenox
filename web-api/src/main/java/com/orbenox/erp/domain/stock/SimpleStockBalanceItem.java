package com.orbenox.erp.domain.stock;

import java.math.BigDecimal;

public record SimpleStockBalanceItem(
        Long productId,
        Long warehouseId,
        BigDecimal quantity,
        BigDecimal reservedQuantity,
        BigDecimal freeQuantity
) {
}

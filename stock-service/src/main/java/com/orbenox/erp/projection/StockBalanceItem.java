package com.orbenox.erp.projection;

import java.math.BigDecimal;

public interface StockBalanceItem {
    Long getProductId();

    Long getWarehouseId();

    BigDecimal getQuantity();

    BigDecimal getReservedQuantity();

    BigDecimal getFreeQuantity();
}

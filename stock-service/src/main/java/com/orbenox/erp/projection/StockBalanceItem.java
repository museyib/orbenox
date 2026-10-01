package com.orbenox.erp.projection;

import java.math.BigDecimal;

public interface StockBalanceItem {
    SimpleProductItem getProduct();
    WarehouseItem getWarehouse();
    BigDecimal getQuantity();
    BigDecimal getReservedQuantity();
    BigDecimal getFreeQuantity();
}

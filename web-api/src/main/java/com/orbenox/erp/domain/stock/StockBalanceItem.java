package com.orbenox.erp.domain.stock;

import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import com.orbenox.erp.domain.warehouse.WarehouseItem;

import java.math.BigDecimal;

public interface StockBalanceItem {
    SimpleProductItem getProduct();
    WarehouseItem getWarehouse();
    BigDecimal getQuantity();
    BigDecimal getReservedQuantity();
    BigDecimal getFreeQuantity();
}
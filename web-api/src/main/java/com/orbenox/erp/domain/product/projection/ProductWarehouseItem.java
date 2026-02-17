package com.orbenox.erp.domain.product.projection;

import com.orbenox.erp.domain.warehouse.WarehouseItem;

import java.math.BigDecimal;

public interface ProductWarehouseItem {
    Long getId();
    SimpleProductItem getProduct();
    WarehouseItem getWarehouse();
    BigDecimal getMinQuantity();
    BigDecimal getMaxQuantity();
}

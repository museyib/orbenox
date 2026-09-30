package com.orbenox.erp.stockservice.projection;

import java.math.BigDecimal;

public interface ProductLineItem {
    Long getId();
    Long getProductId();
    Long getSourceWarehouseId();
    Long getTargetWarehouseId();
    BigDecimal getQuantity();
    BigDecimal getUnitPrice();
    BigDecimal getDiscountRatio();
}

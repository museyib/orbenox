package com.orbenox.erp.transaction.projection;

import com.orbenox.erp.domain.product.projection.ProductItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;

import java.math.BigDecimal;

public interface ProductLineItem {
    Long getId();
    SimpleProductItem getProduct();
    BigDecimal getQuantity();
    BigDecimal getUnitPrice();
    BigDecimal getDiscountRatio();
}

package com.orbenox.erp.domain.product.projection;

import com.orbenox.erp.domain.unit.SimpleUnitItem;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public interface ProductUnitItem {
    Long getId();
    SimpleProductItem getProduct();
    SimpleUnitItem getUnit();
    BigDecimal getFactorToBase();
}

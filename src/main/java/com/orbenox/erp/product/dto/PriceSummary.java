package com.orbenox.erp.product.dto;

import java.math.BigDecimal;

public interface PriceSummary {
    Long getId();
    Long getUnitId();
    String getUnitCode();
    Long getPriceListId();
    String getPriceListCode();
    BigDecimal getPrice();
    BigDecimal getFactorToParent();
    Boolean getFixedPrice();
}

package com.orbenox.erp.product.summary;

import java.math.BigDecimal;

public interface PriceLineSummary {
    Long getId();
    String getPriceKey();
    ProductSummary getProduct();
    PriceListSummary getPriceList();
    UnitSummary getUnit();
    BigDecimal getPrice();
    BigDecimal getFactorToParent();
    Boolean getFixedPrice();
    Long getParentId();
}

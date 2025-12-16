package com.orbenox.erp.price;

import java.math.BigDecimal;

public interface PriceListItem {
    Long getId();
    String getCode();
    String getName();
    BigDecimal getFactorToParent();
    Boolean getEnabled();
    Long getParentId();
    CurrencyItem getCurrency();

    interface CurrencyItem {
        Long getId();
        String getCode();
        String getName();
    }
}

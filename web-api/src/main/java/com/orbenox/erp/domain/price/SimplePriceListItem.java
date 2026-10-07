package com.orbenox.erp.domain.price;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public interface SimplePriceListItem {
    Long getId();

    String getCode();

    String getName();

    BigDecimal getFactorToParent();

    Short getRoundLength();
}

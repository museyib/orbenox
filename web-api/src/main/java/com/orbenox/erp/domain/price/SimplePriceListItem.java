package com.orbenox.erp.domain.price;

import com.orbenox.erp.domain.currency.CurrencyItem;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public interface SimplePriceListItem {
    Long getId();

    String getCode();

    String getName();

    BigDecimal getFactorToParent();

    Short getRoundLength();
}

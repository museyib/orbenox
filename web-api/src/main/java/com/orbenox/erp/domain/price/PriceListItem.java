package com.orbenox.erp.domain.price;

import com.orbenox.erp.domain.currency.CurrencyItem;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public interface PriceListItem {
    Long getId();

    String getCode();

    String getName();

    BigDecimal getFactorToParent();

    boolean isEnabled();

    PriceListParent getParent();

    Short getRoundLength();

    CurrencyItem getCurrency();

    interface PriceListParent {
        Long getId();

        String getCode();

        String getName();

        boolean isEnabled();
    }
}

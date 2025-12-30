package com.orbenox.erp.domain.price;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public interface PriceListItem {
    Long getId();

    String getCode();

    String getName();

    BigDecimal getFactorToParent();

    boolean isEnabled();

    PriceListParent getParent();

    CurrencyItem getCurrency();

    Short getRoundLength();

    interface CurrencyItem {
        Long getId();

        String getCode();

        String getName();

        boolean isEnabled();
    }

    interface PriceListParent {
        Long getId();

        String getCode();

        String getName();

        boolean isEnabled();
    }
}

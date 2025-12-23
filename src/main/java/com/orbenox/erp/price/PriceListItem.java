package com.orbenox.erp.price;

import java.math.BigDecimal;

public interface PriceListItem {
    Long getId();

    String getCode();

    String getName();

    BigDecimal getFactorToParent();

    boolean isEnabled();

    PriceListParent getParent();

    CurrencyItem getCurrency();

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

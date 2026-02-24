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

    Short getRoundLength();

    interface PriceListParent {
        Long getId();

        String getCode();

        String getName();

        boolean isEnabled();
    }
}

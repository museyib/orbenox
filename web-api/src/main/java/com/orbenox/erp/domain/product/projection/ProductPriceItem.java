package com.orbenox.erp.domain.product.projection;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public interface ProductPriceItem {
    Long getId();

    String getPriceKey();

    SimpleProductItem getProduct();

    PriceList getPriceList();

    Unit getUnit();

    BigDecimal getPrice();

    BigDecimal getFactorToParent();

    boolean getFixedPrice();

    BigDecimal getDiscountRatioLimit();

    Short getRoundLength();

    PriceList getParent();

    interface PriceList {
        Long getId();

        String getCode();

        String getName();
    }

    interface Unit {
        Long getId();

        String getCode();

        String getName();
    }
}

package com.orbenox.erp.product.projection;

import java.math.BigDecimal;

public interface ProductPriceItem {
    Long getId();

    String getPriceKey();

    Product getProduct();

    PriceList getPriceList();

    Unit getUnit();

    BigDecimal getPrice();

    BigDecimal getFactorToParent();

    Boolean getFixedPrice();

    Long getParentId();

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

    interface Product {
        Long getId();

        String getCode();

        String getName();
    }
}

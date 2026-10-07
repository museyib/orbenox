package com.orbenox.erp.domain.product.projection;

import com.orbenox.erp.domain.unit.SimpleUnitItem;

public interface SimpleProductItem {
    Long getId();

    SimpleUnitItem getDefaultUnit();

    String getCode();

    String getName();

    String getDescription();

    String getDefaultBarcode();
}

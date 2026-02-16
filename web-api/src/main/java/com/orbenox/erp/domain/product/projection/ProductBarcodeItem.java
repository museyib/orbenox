package com.orbenox.erp.domain.product.projection;

import com.orbenox.erp.domain.unit.SimpleUnitItem;

@SuppressWarnings("unused")
public interface ProductBarcodeItem {
    Long getId();
    SimpleProductItem getProduct();
    SimpleUnitItem getUnit();
    String getBarcode();
}

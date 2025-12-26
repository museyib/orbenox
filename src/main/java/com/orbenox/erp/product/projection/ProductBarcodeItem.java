package com.orbenox.erp.product.projection;

import com.orbenox.erp.unit.SimpleUnitItem;

@SuppressWarnings("unused")
public interface ProductBarcodeItem {
    ProductItem getProduct();
    SimpleUnitItem getUnit();
    String getBarcode();
}

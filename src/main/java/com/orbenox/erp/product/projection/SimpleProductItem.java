package com.orbenox.erp.product.projection;

public interface SimpleProductItem {
    Long getId();

    String getCode();

    String getName();

    String getDescription();

    String getDefaultBarcode();

    boolean isEnabled();
}

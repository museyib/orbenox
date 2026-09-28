package com.orbenox.erp.stockservice.projection;

public interface SimpleProductItem {
    Long getId();

    String getCode();

    String getName();

    String getDescription();

    String getDefaultBarcode();
}

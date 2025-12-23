package com.orbenox.erp.product.projection;

public interface ProductTypeItem {
    Long getId();

    String getCode();

    String getName();

    String getDescription();

    boolean isEnabled();
}
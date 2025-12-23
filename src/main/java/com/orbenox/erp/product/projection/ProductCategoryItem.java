package com.orbenox.erp.product.projection;

public interface ProductCategoryItem {
    Long getId();

    String getCode();

    String getName();

    String getDescription();

    boolean isEnabled();
}
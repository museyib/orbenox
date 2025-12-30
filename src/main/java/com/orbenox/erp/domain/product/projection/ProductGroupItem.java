package com.orbenox.erp.domain.product.projection;

public interface ProductGroupItem {
    Long getId();

    String getCode();

    String getName();

    String getDescription();

    boolean isEnabled();

    SimpleProductGroupItem getParent();
}
package com.orbenox.erp.domain.product.projection;

public interface ProductClassItem {
    Long getId();

    String getCode();

    String getName();

    String getDescription();

    boolean isEnabled();
}
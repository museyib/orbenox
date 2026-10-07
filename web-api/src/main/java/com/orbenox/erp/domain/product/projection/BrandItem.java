package com.orbenox.erp.domain.product.projection;

public interface BrandItem {
    Long getId();

    String getCode();

    String getName();

    String getDescription();

    boolean isEnabled();
}
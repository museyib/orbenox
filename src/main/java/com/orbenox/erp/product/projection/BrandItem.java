package com.orbenox.erp.product.projection;

public interface BrandItem {
    Long getId();
    String getCode();
    String getName();
    String getDescription();
    boolean isEnabled();
}
package com.orbenox.erp.product.projection;

public interface ProductClassItem {
    Long getId();
    String getCode();
    String getName();
    String getDescription();
    boolean isEnabled();
}
package com.orbenox.erp.product.projection;

public interface ProductGroupItem {
    Long getId();
    String getCode();
    String getName();
    String getDescription();
    boolean isEnabled();
    Parent getParent();

    interface Parent {
        Long getId();
        String getCode();
        String getName();
        boolean isEnabled();
    }
}
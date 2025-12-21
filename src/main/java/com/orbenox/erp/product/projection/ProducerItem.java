package com.orbenox.erp.product.projection;

public interface ProducerItem {
    Long getId();
    String getCode();
    String getName();
    String getDescription();
    boolean isEnabled();
}
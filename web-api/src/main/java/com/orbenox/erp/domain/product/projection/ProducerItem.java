package com.orbenox.erp.domain.product.projection;

public interface ProducerItem {
    Long getId();

    String getCode();

    String getName();

    String getDescription();

    boolean isEnabled();
}
package com.orbenox.erp.product.projection;

public interface ProductItem {
    Long getId();
    String getCode();
    String getName();
    Brand getBrand();

    interface Brand {
        Long getId();
        String getName();
    }
}

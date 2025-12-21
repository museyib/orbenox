package com.orbenox.erp.product.projection;

import com.orbenox.erp.common.country.CountryItem;

public interface ProductItem {
    Long getId();
    String getCode();
    String getName();

    String getDescription();

    String getDefaultBarcode();

    boolean isEnabled();

    BrandItem getBrand();

    ProducerItem getProducer();

    ProductTypeItem getProductType();

    ProductCategoryItem getProductCategory();

    ProductClassItem getProductClass();

    ProductGroupItem getProductGroup();

    CountryItem getCountry();

    Unit getDefaultUnit();

    interface Unit {
        Long getId();

        String getCode();
        String getName();

        boolean isEnabled();
    }
}

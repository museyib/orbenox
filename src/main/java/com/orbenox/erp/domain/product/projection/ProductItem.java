package com.orbenox.erp.domain.product.projection;

import com.orbenox.erp.domain.country.CountryItem;
import com.orbenox.erp.domain.unit.SimpleUnitItem;

@SuppressWarnings("unused")
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

    SimpleProductGroupItem getProductGroup();

    CountryItem getCountry();

    SimpleUnitItem getDefaultUnit();
}

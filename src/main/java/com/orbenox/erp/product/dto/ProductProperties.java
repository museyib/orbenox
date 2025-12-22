package com.orbenox.erp.product.dto;

import com.orbenox.erp.common.country.CountryItem;
import com.orbenox.erp.product.projection.*;
import com.orbenox.erp.unit.SimpleUnitItem;
import lombok.Data;

import java.util.List;

@Data
public class ProductProperties {
    private List<BrandItem> brands;
    private List<ProducerItem> producers;
    private List<ProductTypeItem> productTypes;
    private List<ProductClassItem> productClasses;
    private List<ProductCategoryItem> productCategories;
    private List<ProductGroupItem> productGroups;
    private List<CountryItem> countries;
    private List<SimpleUnitItem> units;
}

package com.orbenox.erp.domain.product.projection;

import lombok.Data;

import java.util.List;

@Data
public class ProductUnitData {
    private SimpleProductItem product;
    private List<ProductUnitItem> units;
}

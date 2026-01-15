package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.projection.ProductUnitItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import lombok.Data;

import java.util.List;

@Data
public class ProductUnitData {
    private SimpleProductItem product;
    private List<ProductUnitItem> units;
}

package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.projection.ProductWarehouseItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import lombok.Data;

import java.util.List;

@Data
public class ProductWarehouseData {
    private SimpleProductItem product;
    private List<ProductWarehouseItem> warehouses;
}

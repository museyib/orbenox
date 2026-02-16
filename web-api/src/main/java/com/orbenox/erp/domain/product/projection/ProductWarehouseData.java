package com.orbenox.erp.domain.product.projection;

import lombok.Data;

import java.util.List;

@Data
public class ProductWarehouseData {
    private SimpleProductItem product;
    private List<ProductWarehouseItem> warehouses;
}

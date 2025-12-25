package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.projection.ProductPriceItem;
import com.orbenox.erp.product.projection.SimpleProductItem;
import lombok.Data;

import java.util.List;

@Data
public class ProductPricingData {
    private SimpleProductItem product;
    private List<ProductPriceItem> prices;
}

package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.projection.ProductPriceItem;
import lombok.Data;

import java.util.List;

@Data
public class ProductPricingData {
    private ProductPriceItem.Product product;
    private List<ProductPriceItem> prices;
}

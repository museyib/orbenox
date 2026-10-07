package com.orbenox.erp.domain.product.projection;

import lombok.Data;

import java.util.List;

@Data
public class ProductPricingData {
    private SimpleProductItem product;
    private List<ProductPriceItem> prices;
}

package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.projection.ProductPriceItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import lombok.Data;

import java.util.List;

@Data
public class ProductPricingData {
    private SimpleProductItem product;
    private List<ProductPriceItem> prices;
}

package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.summary.PriceLineSummary;
import com.orbenox.erp.product.summary.ProductSummary;
import lombok.Data;

import java.util.List;

@Data
public class ProductPricingData {
    private ProductSummary product;
    private List<PriceLineSummary> prices;
}

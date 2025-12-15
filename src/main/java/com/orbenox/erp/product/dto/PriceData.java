package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.request.ProductRequest;
import com.orbenox.erp.product.summary.PriceSummary;
import com.orbenox.erp.product.summary.ProductSummary;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PriceData {
    private ProductSummary product;
    private Map<String, List<PriceSummary>> priceMap;
}

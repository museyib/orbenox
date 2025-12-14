package com.orbenox.erp.product.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PriceData {
    private Long productId;
    private Map<Long, List<PriceSummary>> priceMap;
}

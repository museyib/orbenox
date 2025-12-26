package com.orbenox.erp.product.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPriceRequest {
    private Long id;
    private String priceKey;
    private PriceListRequest priceList;
    private UnitRequest unit;
    private BigDecimal price;
    private BigDecimal factorToParent;
    private Boolean fixedPrice;
    private Short roundLength;
    private BigDecimal discountRatioLimit;
}

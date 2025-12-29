package com.orbenox.erp.product.dto;

import com.orbenox.erp.price.PriceListDto;
import com.orbenox.erp.unit.UnitDto;

import java.math.BigDecimal;

public record ProductPriceDto(Long id, ProductDto product, PriceListDto priceList, UnitDto unit, BigDecimal price,
                              BigDecimal factorToParent, Boolean fixedPrice, Short roundLength,
                              BigDecimal discountRatioLimit) {
}

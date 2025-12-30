package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.price.PriceListDto;
import com.orbenox.erp.domain.unit.UnitDto;

import java.math.BigDecimal;

public record ProductPriceDto(Long id, ProductDto product, PriceListDto priceList, UnitDto unit, BigDecimal price,
                              BigDecimal factorToParent, Boolean fixedPrice, Short roundLength,
                              BigDecimal discountRatioLimit) {
}

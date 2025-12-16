package com.orbenox.erp.product.dto;

import com.orbenox.erp.price.PriceListDto;
import com.orbenox.erp.product.entity.ProductPrice;
import com.orbenox.erp.unit.UnitDto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link ProductPrice}
 */
public record ProductPriceDto(Long id, ProductDto product, PriceListDto priceList, UnitDto unit, BigDecimal price,
                              BigDecimal factorToParent, Boolean fixedPrice) implements Serializable {
}
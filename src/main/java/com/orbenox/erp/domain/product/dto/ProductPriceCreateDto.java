package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.price.PriceListDto;
import com.orbenox.erp.domain.unit.UnitDto;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductPriceCreateDto(@NotNull(message = "{product.notNull}") ProductDto product,
                                    @NotNull(message = "{priceList.notNull}") PriceListDto priceList,
                                    @NotNull(message = "{unit.notNull}") UnitDto unit,
                                    BigDecimal price,
                                    BigDecimal factorToParent,
                                    Boolean fixedPrice,
                                    Short roundLength,
                                    BigDecimal discountRatioLimit) {
}

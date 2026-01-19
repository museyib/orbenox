package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.price.PriceListUpdateDto;
import com.orbenox.erp.domain.unit.UnitUpdateDto;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductPriceCreateDto(@NotNull(message = "{product.notNull}") ProductUpdateDto product,
                                    @NotNull(message = "{priceList.notNull}") PriceListUpdateDto priceList,
                                    @NotNull(message = "{unit.notNull}") UnitUpdateDto unit,
                                    BigDecimal price,
                                    BigDecimal factorToParent,
                                    Boolean fixedPrice,
                                    Short roundLength,
                                    BigDecimal discountRatioLimit) {
}

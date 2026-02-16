package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.validation.PositivePrice;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductPriceCreateDto(@NotNull(message = "{product.notNull}") Long productId,
                                    @NotNull(message = "{priceList.notNull}") Long priceListId,
                                    @NotNull(message = "{unit.notNull}") Long unitId,
                                    @PositivePrice BigDecimal price,
                                    BigDecimal factorToParent,
                                    boolean fixedPrice,
                                    Short roundLength,
                                    BigDecimal discountRatioLimit) {
}

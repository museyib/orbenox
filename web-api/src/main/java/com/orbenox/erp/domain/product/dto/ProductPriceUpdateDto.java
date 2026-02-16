package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.validation.PositivePrice;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

public record ProductPriceUpdateDto(Long id,
                                    @NotNull(message = "{product.notNull}") Long productId,
                                    @NotNull(message = "{priceList.notNull}") Long priceListId,
                                    @NotNull(message = "{unit.notNull}") Long unitId,
                                    @PositivePrice BigDecimal price,
                                    BigDecimal factorToParent,
                                    boolean fixedPrice,
                                    Short roundLength,
                                    BigDecimal discountRatioLimit) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductPriceUpdateDto that = (ProductPriceUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

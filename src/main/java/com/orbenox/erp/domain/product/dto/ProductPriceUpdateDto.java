package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.price.PriceListUpdateDto;
import com.orbenox.erp.domain.unit.UnitUpdateDto;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

public record ProductPriceUpdateDto(Long id,
                                    @NotNull(message = "{product.notNull}") ProductUpdateDto product,
                                    @NotNull(message = "{priceList.notNull}") PriceListUpdateDto priceList,
                                    @NotNull(message = "{unit.notNull}") UnitUpdateDto unit,
                                    BigDecimal price,
                                    BigDecimal factorToParent,
                                    Boolean fixedPrice,
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

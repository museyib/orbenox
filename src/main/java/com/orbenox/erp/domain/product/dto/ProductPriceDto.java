package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.price.PriceListDto;
import com.orbenox.erp.domain.unit.UnitDto;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

public record ProductPriceDto(Long id,
                              @NotNull(message = "{product.notNull}") ProductDto product,
                              @NotNull(message = "{priceList.notNull}") PriceListDto priceList,
                              @NotNull(message = "{unit.notNull}") UnitDto unit,
                              BigDecimal price,
                              BigDecimal factorToParent,
                              Boolean fixedPrice,
                              Short roundLength,
                              BigDecimal discountRatioLimit) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductPriceDto that = (ProductPriceDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

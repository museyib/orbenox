package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductUnit;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO for {@link ProductUnit}
 */

public record ProductUnitUpdateDto(Long id,
                                   @NotNull(message = "{product.notNull}") Long productId,
                                   @NotNull(message = "{unit.notNull}") Long unitId,
                                   BigDecimal factorToBase) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductUnitUpdateDto that = (ProductUnitUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductUnit;
import com.orbenox.erp.domain.unit.UnitUpdateDto;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO for {@link ProductUnit}
 */

public record ProductUnitUpdateDto(Long id,
                                   @NotNull(message = "{product.notNull}") ProductUpdateDto product,
                                   @NotNull(message = "{unit.notNull}") UnitUpdateDto unit,
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
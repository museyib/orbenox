package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductUnit;
import com.orbenox.erp.domain.unit.UnitUpdateDto;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for {@link ProductUnit}
 */

public record ProductUnitCreateDto(@NotNull(message = "{product.notNull}") ProductUpdateDto product,
                                   @NotNull(message = "{unit.notNull}") UnitUpdateDto unit,
                                   BigDecimal factorToBase) {
}

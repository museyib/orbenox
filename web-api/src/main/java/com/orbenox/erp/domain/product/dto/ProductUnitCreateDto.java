package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductUnit;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for {@link ProductUnit}
 */

public record ProductUnitCreateDto(@NotNull(message = "{product.notNull}") Long productId,
                                   @NotNull(message = "{unit.notNull}") Long unitId,
                                   BigDecimal factorToBase) {
}

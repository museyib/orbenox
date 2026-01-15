package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductUnit;
import com.orbenox.erp.domain.unit.UnitDto;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for {@link ProductUnit}
 */

public record ProductUnitDto(Long id,
                             @NotNull(message = "{product.notNull}") ProductDto product,
                             @NotNull(message = "{unit.notNull}") UnitDto unit,
                             BigDecimal factorToBase) {
}

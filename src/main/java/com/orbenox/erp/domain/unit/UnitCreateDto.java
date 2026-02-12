package com.orbenox.erp.domain.unit;

import com.orbenox.erp.domain.unit.unitdimension.UnitDimensionDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for {@link Unit}
 */
public record UnitCreateDto(boolean enabled,
                            @NotBlank(message = "{code.notBlank}") String code,
                            @NotBlank(message = "{name.notBlank}") String name,
                            @NotNull(message = "{unitDimension.notNull}") UnitDimensionDto unitDimension,
                            boolean base,
                            BigDecimal factorToBase,
                            BigDecimal offsetToBase) {
}
package com.orbenox.erp.domain.unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for {@link Unit}
 */
public record UnitCreateDto(boolean enabled,
                            @NotBlank(message = "{code.notBlank}") String code,
                            @NotBlank(message = "{name.notBlank}") String name,
                            @NotNull(message = "{unitDimension.notNull}") Long unitDimensionId,
                            boolean base,
                            BigDecimal factorToBase,
                            BigDecimal offsetToBase) {
}

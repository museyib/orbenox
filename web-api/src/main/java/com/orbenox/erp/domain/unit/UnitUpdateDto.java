package com.orbenox.erp.domain.unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO for {@link Unit}
 */
public record UnitUpdateDto(Long id,
                            boolean enabled,
                            @NotBlank(message = "{code.notBlank}") String code,
                            @NotBlank(message = "{name.notBlank}") String name,
                            @NotNull(message = "{unitDimension.notNull}") Long unitDimensionId,
                            boolean base,
                            BigDecimal factorToBase,
                            BigDecimal offsetToBase) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UnitUpdateDto dto = (UnitUpdateDto) o;
        return Objects.equals(id, dto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

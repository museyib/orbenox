package com.orbenox.erp.domain.unit.unitdimension;


import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * DTO for {@link UnitDimension}
 */
public record UnitDimensionDto(Long id,
                               boolean enabled,
                               @NotBlank(message = "{code.notBlank}") String code,
                               @NotBlank(message = "{name.notBlank}") String name) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UnitDimensionDto that = (UnitDimensionDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
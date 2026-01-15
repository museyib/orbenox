package com.orbenox.erp.domain.unit.unitdimension;


import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link UnitDimension}
 */
public record UnitDimensionDto(Long id,
                               Boolean enabled,
                               @NotBlank(message = "{code.notBlank}") String code,
                               @NotBlank(message = "{name.notBlank}") String name) {
}
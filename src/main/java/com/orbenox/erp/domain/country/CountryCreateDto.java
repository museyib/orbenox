package com.orbenox.erp.domain.country;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link Country}
 */
public record CountryCreateDto(Boolean enabled,
                               @NotBlank(message = "{code.notBlank}") String code,
                               @NotBlank(message = "{name.notBlank}") String name) {
}
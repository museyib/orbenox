package com.orbenox.erp.domain.currency;


import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link Currency}
 */
public record CurrencyDto(Long id,
                          Boolean enabled,
                          @NotBlank(message = "{code.notBlank}") String code,
                          @NotBlank(message = "{name.notBlank}") String name) {
}
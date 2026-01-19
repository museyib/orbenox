package com.orbenox.erp.domain.transaction;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link TransactionType}
 */
public record TransactionTypeCreateDto(Boolean enabled,
                                       @NotBlank(message = "{code.notBlank}") String code,
                                       @NotBlank(message = "{name.notBlank}") String name) {
}
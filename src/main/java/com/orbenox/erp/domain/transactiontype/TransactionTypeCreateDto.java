package com.orbenox.erp.domain.transactiontype;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link TransactionType}
 */
public record TransactionTypeCreateDto(Boolean enabled,
                                       @NotBlank(message = "{code.notBlank}") String code,
                                       @NotBlank(message = "{name.notBlank}") String name) {
}
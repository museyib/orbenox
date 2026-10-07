package com.orbenox.erp.domain.account;

import com.orbenox.erp.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link Account}
 */
public record AccountCreateDto(boolean enabled,
                               @NotBlank(message = "{code.notBlank}") String code,
                               @NotBlank(message = "{name.notBlank}") String name,
                               @NotNull(message = "{accountType.notNull}") AccountType accountType) {
}

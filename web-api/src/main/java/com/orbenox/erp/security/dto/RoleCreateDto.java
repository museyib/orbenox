package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.AppRole;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link AppRole}
 */
public record RoleCreateDto(boolean enabled,
                            @NotBlank(message = "{code.notBlank}") String code,
                            @NotBlank(message = "{name.notBlank}") String name) {
}

package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.AppUser;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * DTO for {@link AppUser}
 */

public record UserCreateDto(boolean enabled,
                            String username,
                            String password,
                            String displayName,
                            @NotNull(message = "{userType.notNull}") Long userTypeId,
                            Set<Long> roleIds) {
}

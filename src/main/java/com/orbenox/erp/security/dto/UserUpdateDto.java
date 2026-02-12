package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.AppUser;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link AppUser}
 */

public record UserUpdateDto(Long id,
                            boolean enabled,
                            String username,
                            String password,
                            String displayName,
                            @NotNull(message = "{userType.notNull}") UserTypeDto userType,
                            Set<RoleUpdateDto> roles) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserUpdateDto userDto = (UserUpdateDto) o;
        return Objects.equals(id, userDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

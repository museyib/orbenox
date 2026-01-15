package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.AppUser;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link AppUser}
 */

public record UserDto(Long id,
                      Boolean enabled,
                      String username,
                      String password,
                      String displayName,
                      @NotNull(message = "{userType.notNull}") UserTypeDto userType,
                      Set<RoleDto> roles) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.enums.UserType;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link AppUser}
 */

public record UserDto(Long id, Boolean enabled, String username, String displayName, UserType userType,
                      Set<RoleDto> roles) implements Serializable {
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

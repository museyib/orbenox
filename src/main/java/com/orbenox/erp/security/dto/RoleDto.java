package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.AppRole;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link AppRole}
 */
public record RoleDto(Long id, Boolean enabled, String code, String name) implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoleDto roleDto = (RoleDto) o;
        return Objects.equals(id, roleDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.AppPermission;

import java.util.Objects;


/**
 * DTO for {@link AppPermission}
 */
public record RolePermissionDto(Long id,
                                boolean enabled,
                                Long roleId,
                                Long resourceId,
                                String action) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionDto that = (RolePermissionDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

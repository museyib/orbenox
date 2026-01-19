package com.orbenox.erp.security.dto;

import com.orbenox.erp.domain.action.ActionDto;
import com.orbenox.erp.domain.resource.ResourceUpdateDto;
import com.orbenox.erp.security.entity.AppPermission;

import java.util.Objects;


/**
 * DTO for {@link AppPermission}
 */
public record RolePermissionDto(Long id,
                                Boolean enabled,
                                RoleUpdateDto appRole,
                                ResourceUpdateDto resource,
                                ActionDto action) {
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

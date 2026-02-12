package com.orbenox.erp.security.dto;

import com.orbenox.erp.domain.resource.ResourceUpdateDto;
import com.orbenox.erp.security.entity.AppPermission;

import java.util.Objects;


/**
 * DTO for {@link AppPermission}
 */
public record UserPermissionDto(Long id,
                                boolean enabled,
                                UserUpdateDto appUser,
                                ResourceUpdateDto resource,
                                String action) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserPermissionDto that = (UserPermissionDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

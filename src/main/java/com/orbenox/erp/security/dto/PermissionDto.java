package com.orbenox.erp.security.dto;

import com.orbenox.erp.domain.action.ActionDto;
import com.orbenox.erp.domain.resource.ResourceDto;
import com.orbenox.erp.security.entity.AppPermission;



/**
 * DTO for {@link AppPermission}
 */
public record PermissionDto(Long id, ResourceDto resource, ActionDto action, Boolean enabled) {
    public String getPermissionCode() {
        return resource.code() + ":" + action.code();
    }
}

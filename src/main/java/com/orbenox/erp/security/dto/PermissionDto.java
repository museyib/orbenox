package com.orbenox.erp.security.dto;

import com.orbenox.erp.common.action.ActionDto;
import com.orbenox.erp.common.resource.ResourceDto;
import com.orbenox.erp.security.entity.AppPermission;

import java.io.Serializable;

/**
 * DTO for {@link AppPermission}
 */
public record PermissionDto(Long id, ResourceDto resource, ActionDto action, Boolean enabled) implements Serializable {
    public String getPermissionCode() {
        return resource.code() + ":" + action.code();
    }
}

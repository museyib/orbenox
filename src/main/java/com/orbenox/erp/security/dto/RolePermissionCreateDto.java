package com.orbenox.erp.security.dto;

import com.orbenox.erp.domain.action.ActionDto;
import com.orbenox.erp.domain.resource.ResourceDto;
import com.orbenox.erp.security.entity.AppPermission;


/**
 * DTO for {@link AppPermission}
 */
public record RolePermissionCreateDto(Boolean enabled,
                                      RoleDto appRole,
                                      ResourceDto resource,
                                      ActionDto action) {
}

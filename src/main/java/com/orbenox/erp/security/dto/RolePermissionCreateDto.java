package com.orbenox.erp.security.dto;

import com.orbenox.erp.domain.resource.ResourceUpdateDto;
import com.orbenox.erp.security.entity.AppPermission;


/**
 * DTO for {@link AppPermission}
 */
public record RolePermissionCreateDto(boolean enabled,
                                      RoleUpdateDto appRole,
                                      ResourceUpdateDto resource,
                                      String action) {
}

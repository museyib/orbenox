package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.AppPermission;


/**
 * DTO for {@link AppPermission}
 */
public record RolePermissionCreateDto(boolean enabled,
                                      Long roleId,
                                      Long resourceId,
                                      String action) {
}

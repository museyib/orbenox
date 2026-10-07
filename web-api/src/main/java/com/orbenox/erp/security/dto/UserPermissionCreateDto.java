package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.AppPermission;


/**
 * DTO for {@link AppPermission}
 */
public record UserPermissionCreateDto(boolean enabled,
                                      Long userId,
                                      Long resourceId,
                                      String action) {
}

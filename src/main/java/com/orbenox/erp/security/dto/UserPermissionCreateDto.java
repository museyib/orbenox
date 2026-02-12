package com.orbenox.erp.security.dto;

import com.orbenox.erp.domain.resource.ResourceUpdateDto;
import com.orbenox.erp.security.entity.AppPermission;


/**
 * DTO for {@link AppPermission}
 */
public record UserPermissionCreateDto(boolean enabled,
                                      UserUpdateDto appUser,
                                      ResourceUpdateDto resource,
                                      String action) {
}

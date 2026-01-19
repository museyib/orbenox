package com.orbenox.erp.security.dto;

import com.orbenox.erp.domain.action.ActionDto;
import com.orbenox.erp.domain.resource.ResourceUpdateDto;
import com.orbenox.erp.security.entity.AppPermission;


/**
 * DTO for {@link AppPermission}
 */
public record UserPermissionCreateDto(Boolean enabled,
                                      UserUpdateDto appUser,
                                      ResourceUpdateDto resource,
                                      ActionDto action) {
}

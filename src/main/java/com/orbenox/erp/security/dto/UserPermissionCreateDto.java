package com.orbenox.erp.security.dto;

import com.orbenox.erp.domain.action.ActionDto;
import com.orbenox.erp.domain.resource.ResourceDto;
import com.orbenox.erp.security.entity.AppPermission;


/**
 * DTO for {@link AppPermission}
 */
public record UserPermissionCreateDto(Boolean enabled,
                                      UserDto appUser,
                                      ResourceDto resource,
                                      ActionDto action) {
}

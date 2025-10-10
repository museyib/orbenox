package com.orbenox.erp.security.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.orbenox.erp.security.entity.AppUserRole}
 */
@Value
public class AppUserRoleDto implements Serializable {
    AppUserRoleIdDto id;
    UserDto appUser;
    RoleDto appRole;
}
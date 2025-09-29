package com.orbenox.erp.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String username;
    private String displayName;
    private String userType;
    private Set<RoleDto> roles;
}

package com.orbenox.erp.security.dto;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionDto {
    private Long roleId;
    private List<PermissionDto> permissions;
}

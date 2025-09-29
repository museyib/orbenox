package com.orbenox.erp.security.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserPermissionDto {
    private Long userId;
    private List<PermissionDto> permissions;
}

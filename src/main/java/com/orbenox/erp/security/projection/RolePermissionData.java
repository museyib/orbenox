package com.orbenox.erp.security.projection;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionData {
    private RoleItem role;
    private List<PermissionItem> permissions;
}

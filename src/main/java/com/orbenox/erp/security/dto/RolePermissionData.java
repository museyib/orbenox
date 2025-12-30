package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.projection.PermissionItem;
import com.orbenox.erp.security.projection.RoleItem;
import lombok.Data;

import java.util.List;

@Data
public class RolePermissionData {
    private RoleItem role;
    private List<PermissionItem> permissions;
}

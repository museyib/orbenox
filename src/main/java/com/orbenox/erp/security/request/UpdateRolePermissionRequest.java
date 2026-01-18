package com.orbenox.erp.security.request;

import com.orbenox.erp.security.dto.RolePermissionCreateDto;
import com.orbenox.erp.security.dto.RolePermissionDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRolePermissionRequest {
    private Long roleId;
    private List<RolePermissionCreateDto> permissionsToInsert;
    private List<RolePermissionDto> permissionsToDelete;
}

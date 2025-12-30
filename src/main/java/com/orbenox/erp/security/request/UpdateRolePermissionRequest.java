package com.orbenox.erp.security.request;

import com.orbenox.erp.security.dto.PermissionDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRolePermissionRequest {
    private Long roleId;
    private List<PermissionDto> permissions;
}

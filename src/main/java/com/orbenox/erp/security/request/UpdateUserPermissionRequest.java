package com.orbenox.erp.security.request;

import com.orbenox.erp.security.dto.UserPermissionCreateDto;
import com.orbenox.erp.security.dto.UserPermissionDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateUserPermissionRequest {
    private Long userId;
    private List<UserPermissionCreateDto> permissionsToInsert;
    private List<UserPermissionDto> permissionsToDelete;
}

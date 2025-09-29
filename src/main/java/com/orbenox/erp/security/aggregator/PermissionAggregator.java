package com.orbenox.erp.security.aggregator;

import com.orbenox.erp.security.dto.RolePermissionDto;
import com.orbenox.erp.security.dto.UserPermissionDto;
import com.orbenox.erp.security.dto.mapper.PermissionMapper;
import com.orbenox.erp.security.entity.AppPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionAggregator {
    private final PermissionMapper permissionMapper;

    public UserPermissionDto toUserPermissionDto(Long userId, List<AppPermission> permissions) {
        UserPermissionDto dto = new UserPermissionDto();
        dto.setUserId(userId);
        dto.setPermissions(permissions.stream().map(permissionMapper::toDto).toList());
        return dto;
    }

    public RolePermissionDto toRolePermissionDto(Long roleId, List<AppPermission> permissions) {
        RolePermissionDto dto = new RolePermissionDto();
        dto.setRoleId(roleId);
        dto.setPermissions(permissions.stream().map(permissionMapper::toDto).toList());
        return dto;
    }

    public List<AppPermission> toPermissionList(UserPermissionDto dto) {
        return dto.getPermissions().stream().map(permissionMapper::toEntity).toList();
    }

    public List<AppPermission> toPermissionList(RolePermissionDto dto) {
        return dto.getPermissions().stream().map(permissionMapper::toEntity).toList();
    }
}

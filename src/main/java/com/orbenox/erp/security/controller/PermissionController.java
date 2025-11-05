package com.orbenox.erp.security.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.common.action.ActionDto;
import com.orbenox.erp.security.dto.RolePermissionDto;
import com.orbenox.erp.security.dto.UserPermissionDto;
import com.orbenox.erp.security.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PreAuthorize("hasPermission('APP_PERMISSION', 'READ')")
    @GetMapping("/user/{id}")
    public ResponseEntity<Response<UserPermissionDto>> findByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(permissionService.getUserPermission(id)));
    }

    @GetMapping("/availableForUser")
    public ResponseEntity<Response<List<ActionDto>>> getAvailableForUser(@RequestParam Long userId,
                                                                         @RequestParam Long resourceId) {
        return ResponseEntity.ok(Response.successData(permissionService.getGrantablePermissionsForUser(userId, resourceId)));
    }

    @PatchMapping("/user")
    public ResponseEntity<Response<UserPermissionDto>>  updateUserPermissions(@RequestBody UserPermissionDto permissions) {
        return ResponseEntity.ok(Response.successData(permissionService.updateUserPermissions(permissions)));
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<Response<RolePermissionDto>> findByRoleId(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(permissionService.getRolePermission(id)));
    }

    @GetMapping("/availableForRole")
    public ResponseEntity<Response<List<ActionDto>>> getAvailableForRole(@RequestParam Long roleId,
                                                                         @RequestParam Long resourceId) {
        return ResponseEntity.ok(Response.successData(permissionService.getGrantablePermissionsForRole(roleId, resourceId)));
    }

    @PatchMapping("/role")
    public ResponseEntity<Response<UserPermissionDto>>  updateRolePermissions(@RequestBody RolePermissionDto permissions) {
        return ResponseEntity.ok(Response.successData(permissionService.updateRolePermissions(permissions)));
    }
}

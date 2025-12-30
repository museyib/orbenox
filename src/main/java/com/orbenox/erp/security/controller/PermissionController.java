package com.orbenox.erp.security.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.action.ActionItem;
import com.orbenox.erp.security.request.UpdateRolePermissionRequest;
import com.orbenox.erp.security.request.UpdateUserPermissionRequest;
import com.orbenox.erp.security.dto.RolePermissionData;
import com.orbenox.erp.security.dto.UserPermissionData;
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
    public ResponseEntity<Response<UserPermissionData>> findByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(permissionService.getDirectUserPermission(id)));
    }

    @PreAuthorize("hasPermission('APP_PERMISSION', 'READ')")
    @GetMapping("/availableForUser")
    public ResponseEntity<Response<List<ActionItem>>> getAvailableForUser(@RequestParam Long userId,
                                                                          @RequestParam Long resourceId) {
        return ResponseEntity.ok(Response.successData(permissionService.getGrantablePermissionsForUser(userId, resourceId)));
    }

    @PreAuthorize("hasPermission('APP_PERMISSION', 'UPDATE')")
    @PatchMapping("/user")
    public ResponseEntity<Response<UserPermissionData>> updateUserPermissions(@RequestBody UpdateUserPermissionRequest permissions) {
        return ResponseEntity.ok(Response.successData(permissionService.updateUserPermissions(permissions)));
    }

    @PreAuthorize("hasPermission('APP_PERMISSION', 'READ')")
    @GetMapping("/role/{id}")
    public ResponseEntity<Response<RolePermissionData>> findByRoleId(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(permissionService.getRolePermission(id)));
    }

    @PreAuthorize("hasPermission('APP_PERMISSION', 'READ')")
    @GetMapping("/availableForRole")
    public ResponseEntity<Response<List<ActionItem>>> getAvailableForRole(@RequestParam Long roleId,
                                                                          @RequestParam Long resourceId) {
        return ResponseEntity.ok(Response.successData(permissionService.getGrantablePermissionsForRole(roleId, resourceId)));
    }

    @PreAuthorize("hasPermission('APP_PERMISSION', 'UPDATE')")
    @PatchMapping("/role")
    public ResponseEntity<Response<RolePermissionData>> updateRolePermissions(@RequestBody UpdateRolePermissionRequest permissions) {
        return ResponseEntity.ok(Response.successData(permissionService.updateRolePermissions(permissions)));
    }
}

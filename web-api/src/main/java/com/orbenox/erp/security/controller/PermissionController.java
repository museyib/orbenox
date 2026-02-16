package com.orbenox.erp.security.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.security.projection.RolePermissionData;
import com.orbenox.erp.security.projection.UserPermissionData;
import com.orbenox.erp.security.request.UpdateRolePermissionRequest;
import com.orbenox.erp.security.request.UpdateUserPermissionRequest;
import com.orbenox.erp.security.service.PermissionService;
import jakarta.validation.Valid;
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
    @GetMapping("/user/{userId}/availableResourceActions")
    public ResponseEntity<Response<List<String>>> getAvailableActionsForUser(@PathVariable Long userId,
                                                                                 @RequestParam Long resourceId) {
        return ResponseEntity.ok(Response.successData(permissionService.getAvailableActionsForUser(userId, resourceId)));
    }

    @PreAuthorize("hasPermission('APP_PERMISSION', 'UPDATE')")
    @PatchMapping("/user")
    public ResponseEntity<Response<UserPermissionData>> updateUserPermissions(@Valid @RequestBody UpdateUserPermissionRequest request) {
        return ResponseEntity.ok(Response.successData(permissionService.updateUserPermissions(request)));
    }

    @PreAuthorize("hasPermission('APP_PERMISSION', 'READ')")
    @GetMapping("/role/{id}")
    public ResponseEntity<Response<RolePermissionData>> findByRoleId(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(permissionService.getRolePermission(id)));
    }

    @PreAuthorize("hasPermission('APP_PERMISSION', 'READ')")
    @GetMapping("/role/{roleId}/availableResourceActions")
    public ResponseEntity<Response<List<String>>> getAvailableActionsForRole(@PathVariable Long roleId,
                                                                                 @RequestParam Long resourceId) {
        return ResponseEntity.ok(Response.successData(permissionService.getAvailableActionsForRole(roleId, resourceId)));
    }

    @PreAuthorize("hasPermission('APP_PERMISSION', 'UPDATE')")
    @PatchMapping("/role")
    public ResponseEntity<Response<RolePermissionData>> updateRolePermissions(@Valid @RequestBody UpdateRolePermissionRequest request) {
        return ResponseEntity.ok(Response.successData(permissionService.updateRolePermissions(request)));
    }
}

package com.orbenox.erp.security.service;

import com.orbenox.erp.common.action.ActionItem;
import com.orbenox.erp.common.action.ActionRepository;
import com.orbenox.erp.common.resource.ResourceRepository;
import com.orbenox.erp.security.dto.PermissionDto;
import com.orbenox.erp.security.dto.RolePermissionDto;
import com.orbenox.erp.security.dto.UserPermissionDto;
import com.orbenox.erp.security.entity.AppPermission;
import com.orbenox.erp.security.projection.*;
import com.orbenox.erp.security.repository.PermissionRepository;
import com.orbenox.erp.security.repository.RoleRepository;
import com.orbenox.erp.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ResourceRepository resourceRepository;
    private final PermissionRepository permissionRepository;
    private final ActionRepository actionRepository;

    public UserPermissionData getDirectUserPermission(Long userId) {
        UserItem user = userRepository.getItemById(userId);
        List<PermissionItem> permissions = permissionRepository.getPermissionsByUserId(userId);
        UserPermissionData permissionData = new UserPermissionData();
        permissionData.setUser(user);
        permissionData.setPermissions(permissions);
        return permissionData;
    }

    public UserPermissionData getUserPermission(Long userId) {
        List<RoleItem> userRoles = userRepository.getRolesByUserId(userId);
        List<PermissionItem> direct = permissionRepository.getPermissionsByUserId(userId);
        List<PermissionItem> viaRoles = userRoles.stream().flatMap(
                role -> permissionRepository.getPermissionsByRoleId(role.getId()).stream()).toList();
        List<PermissionItem> permissions = Stream.concat(direct.stream(), viaRoles.stream()).toList();
        UserPermissionData permissionData = new UserPermissionData();
        UserItem user = userRepository.getItemById(userId);
        permissionData.setUser(user);
        permissionData.setPermissions(permissions);
        return permissionData;
    }

    public RolePermissionData getRolePermission(Long roleId) {
        RoleItem role = roleRepository.getItemById(roleId);
        List<PermissionItem> permissions = permissionRepository.getPermissionsByRoleId(roleId);
        RolePermissionData permissionData = new RolePermissionData();
        permissionData.setRole(role);
        permissionData.setPermissions(permissions);
        return permissionData;
    }

    public List<ActionItem> getGrantablePermissionsForUser(Long userId, Long resourceId) {
        List<ActionItem> givenPermissions = permissionRepository.getActionItemsByAppUserIdAndResourceId(userId, resourceId);
        List<ActionItem> allPermissions = resourceRepository.getActionItemsByResourceId(resourceId);
        allPermissions.removeAll(givenPermissions);
        return allPermissions;
    }

    public List<ActionItem> getGrantablePermissionsForRole(Long roleId, Long resourceId) {
        List<ActionItem> givenPermissions = permissionRepository.getActionItemsByAppRoleIdAndResourceId(roleId, resourceId);
        List<ActionItem> allPermissions = resourceRepository.getActionItemsByResourceId(resourceId);
        allPermissions.removeAll(givenPermissions);
        return allPermissions;
    }

    @Transactional
    public UserPermissionData updateUserPermissions(UserPermissionDto dto) {
        Long userId = dto.getUserId();
        List<AppPermission> existing = permissionRepository.findByAppUserIdAndDeletedFalse(userId);

        Set<String> incomingCodes = dto.getPermissions().stream()
                .map(PermissionDto::getPermissionCode)
                .collect(Collectors.toSet());

        Set<String> existingCodes = existing.stream().map(AppPermission::getPermissionCode).collect(Collectors.toSet());

        Set<String> toAdd = new HashSet<>(incomingCodes);
        toAdd.removeAll(existingCodes);

        Set<String> toRemove = new HashSet<>(existingCodes);
        toRemove.removeAll(incomingCodes);

        if (!toRemove.isEmpty()) {
            permissionRepository.deleteByAppUserIdAndCodes(userId, toRemove);
        }

        List<AppPermission> appPermissions = dto.getPermissions().stream()
                .filter(p -> toAdd.contains(p.getPermissionCode()))
                .map(p -> {
                    AppPermission appPermission = new AppPermission();
                    appPermission.setAppUser(userRepository.findByIdAndDeletedFalse(userId));
                    appPermission.setResource(resourceRepository.findByIdAndDeletedFalse(p.resource().id()));
                    appPermission.setAction(actionRepository.findByIdAndDeletedFalse(p.action().id()));
                    return appPermission;
                }).collect(Collectors.toList());
        if (!appPermissions.isEmpty()) {
            permissionRepository.saveAll(appPermissions);
        }
        return getUserPermission(userId);
    }

    @Transactional
    public RolePermissionData updateRolePermissions(RolePermissionDto dto) {
        Long roleId = dto.getRoleId();
        List<AppPermission> existing = permissionRepository.findByAppRoleIdAndDeletedFalse(roleId);

        Set<String> incomingCodes = dto.getPermissions().stream()
                .map(PermissionDto::getPermissionCode)
                .collect(Collectors.toSet());

        Set<String> existingCodes = existing.stream().map(AppPermission::getPermissionCode).collect(Collectors.toSet());

        Set<String> toAdd = new HashSet<>(incomingCodes);
        toAdd.removeAll(existingCodes);

        Set<String> toRemove = new HashSet<>(existingCodes);
        toRemove.removeAll(incomingCodes);

        if (!toRemove.isEmpty()) {
            permissionRepository.deleteByAppRoleIdAndCodes(roleId, toRemove);
        }

        List<AppPermission> appPermissions = dto.getPermissions().stream()
                .filter(p -> toAdd.contains(p.getPermissionCode()))
                .map(p -> {
                    AppPermission appPermission = new AppPermission();
                    appPermission.setAppRole(roleRepository.findByIdAndDeletedFalse(roleId));
                    appPermission.setResource(resourceRepository.findByIdAndDeletedFalse(p.resource().id()));
                    appPermission.setAction(actionRepository.findByIdAndDeletedFalse(p.action().id()));
                    return appPermission;
                }).collect(Collectors.toList());
        if (!appPermissions.isEmpty()) {
            permissionRepository.saveAll(appPermissions);
        }
        return getRolePermission(roleId);
    }
}

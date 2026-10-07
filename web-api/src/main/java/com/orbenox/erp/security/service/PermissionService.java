package com.orbenox.erp.security.service;

import com.orbenox.erp.domain.resource.ResourceRepository;
import com.orbenox.erp.security.dto.RolePermissionDto;
import com.orbenox.erp.security.dto.UserPermissionDto;
import com.orbenox.erp.security.entity.AppPermission;
import com.orbenox.erp.security.mapper.RolePermissionMapper;
import com.orbenox.erp.security.mapper.UserPermissionMapper;
import com.orbenox.erp.security.projection.*;
import com.orbenox.erp.security.repository.PermissionRepository;
import com.orbenox.erp.security.repository.RoleRepository;
import com.orbenox.erp.security.repository.UserRepository;
import com.orbenox.erp.security.request.UpdateRolePermissionRequest;
import com.orbenox.erp.security.request.UpdateUserPermissionRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static com.orbenox.erp.config.CacheConfig.CacheNames.*;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ResourceRepository resourceRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionMapper userPermissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Cacheable(PERMISSIONS_USER)
    public UserPermissionData getDirectUserPermission(Long userId) {
        SimpleUserItem user = userRepository.getItemById(userId);
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
        SimpleUserItem user = userRepository.getItemById(userId);
        permissionData.setUser(user);
        permissionData.setPermissions(permissions);
        return permissionData;
    }

    @Cacheable(PERMISSIONS_ROLE)
    public RolePermissionData getRolePermission(Long roleId) {
        RoleItem role = roleRepository.getItemById(roleId);
        List<PermissionItem> permissions = permissionRepository.getPermissionsByRoleId(roleId);
        RolePermissionData permissionData = new RolePermissionData();
        permissionData.setRole(role);
        permissionData.setPermissions(permissions);
        return permissionData;
    }

    @Cacheable(AVAILABLE_RESOURCE_ACTIONS_USER)
    public List<String> getAvailableActionsForUser(Long userId, Long resourceId) {
        List<String> givenPermissions = permissionRepository.getActionItemsByAppUserIdAndResourceId(userId, resourceId);
        List<String> allPermissions = resourceRepository.getActionItemsByResourceId(resourceId);
        allPermissions.removeAll(givenPermissions);
        return allPermissions;
    }

    @Cacheable(AVAILABLE_RESOURCE_ACTIONS_ROLE)
    public List<String> getAvailableActionsForRole(Long roleId, Long resourceId) {
        List<String> givenPermissions = permissionRepository.getActionItemsByAppRoleIdAndResourceId(roleId, resourceId);
        List<String> allPermissions = resourceRepository.getActionItemsByResourceId(resourceId);
        allPermissions.removeAll(givenPermissions);
        return allPermissions;
    }


    /**
     * Updates permissions by fully replacing existing ones.
     * Partial updates are not supported intentionally.
     */
    @CacheEvict(value = {
            HAS_PERMISSION,
            PERMISSIONS_USER,
            AVAILABLE_RESOURCE_ACTIONS_USER}, allEntries = true)
    @Transactional
    public UserPermissionData updateUserPermissions(UpdateUserPermissionRequest request) {
        List<Long> idsToDelete = request.getPermissionsToDelete().stream().map(UserPermissionDto::id).toList();

        List<AppPermission> entityListToInsert = request.getPermissionsToInsert().stream()
                .map(userPermissionMapper::toEntity).toList();
        permissionRepository.saveAll(entityListToInsert);
        permissionRepository.deleteAllById(idsToDelete);

        return getUserPermission(request.getUserId());
    }


    /**
     * Updates permissions by fully replacing existing ones.
     * Partial updates are not supported intentionally.
     */
    @CacheEvict(value = {
            HAS_PERMISSION,
            PERMISSIONS_ROLE,
            AVAILABLE_RESOURCE_ACTIONS_ROLE}, allEntries = true)
    @Transactional
    public RolePermissionData updateRolePermissions(UpdateRolePermissionRequest request) {
        List<Long> idsToDelete = request.getPermissionsToDelete().stream().map(RolePermissionDto::id).toList();

        List<AppPermission> entityListToInsert = request.getPermissionsToInsert().stream()
                .map(rolePermissionMapper::toEntity).toList();
        permissionRepository.saveAll(entityListToInsert);
        permissionRepository.deleteAllById(idsToDelete);

        RoleItem role = roleRepository.getItemById(request.getRoleId());
        List<PermissionItem> permissions = permissionRepository.getPermissionsByRoleId(request.getRoleId());
        RolePermissionData permissionData = new RolePermissionData();
        permissionData.setRole(role);
        permissionData.setPermissions(permissions);
        return permissionData;
    }
}


package com.orbenox.erp.security.service;

import com.orbenox.erp.common.action.Action;
import com.orbenox.erp.common.action.ActionDto;
import com.orbenox.erp.common.action.ActionMapper;
import com.orbenox.erp.common.action.ActionRepository;
import com.orbenox.erp.common.resource.Resource;
import com.orbenox.erp.common.resource.ResourceRepository;
import com.orbenox.erp.security.aggregator.PermissionAggregator;
import com.orbenox.erp.security.dto.PermissionDto;
import com.orbenox.erp.security.dto.RolePermissionDto;
import com.orbenox.erp.security.dto.UserPermissionDto;
import com.orbenox.erp.security.entity.AppPermission;
import com.orbenox.erp.security.entity.AppRole;
import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.entity.AppUserRole;
import com.orbenox.erp.security.repository.AppUserRoleRepository;
import com.orbenox.erp.security.repository.PermissionRepository;
import com.orbenox.erp.security.repository.RoleRepository;
import com.orbenox.erp.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final PermissionAggregator permissionAggregator;
    private final ActionMapper actionMapper;
    private final AppUserRoleRepository appUserRoleRepository;

    public UserPermissionDto getUserPermission(Long userId) {
        List<AppUserRole> userRoles = appUserRoleRepository.findAllByAppUserId(userId);
        List<AppPermission> direct = permissionRepository.findByAppUserIdAndDeletedFalse(userId);
        List<AppPermission> viaRoles = userRoles
                .stream()
                .flatMap(role -> permissionRepository.findByAppRoleIdAndDeletedFalse(role.getAppRole().getId())
                        .stream())
                .toList();
        List<AppPermission> permissions = Stream.concat(direct.stream(), viaRoles.stream()).toList();
        return permissionAggregator.toUserPermissionDto(userId, permissions);
    }

    public RolePermissionDto getRolePermission(Long roleId) {
        List<AppPermission> permissions = permissionRepository.findByAppRoleIdAndDeletedFalse(roleId);
        return permissionAggregator.toRolePermissionDto(roleId, permissions);
    }

    public List<ActionDto> getGrantablePermissionsForUser(Long userId, Long resourceId) {
        AppUser appUser = userRepository.findByIdAndDeletedFalse(userId);
        Resource resource = resourceRepository.findByIdAndDeletedFalse(resourceId);
        List<Action> givenPermissions =
                permissionRepository.findByAppUserIdAndResourceIdAndDeletedFalse(appUser.getId(), resource.getId())
                .stream().map(AppPermission::getAction).toList();
        List<Action> allPermissions = new ArrayList<>(resource.getActions().stream().toList());
        allPermissions.removeAll(givenPermissions);
        return actionMapper.toDTOList(allPermissions);
    }

    public List<ActionDto> getGrantablePermissionsForRole(Long roleId, Long resourceId) {
        AppRole appRole = roleRepository.findByIdAndDeletedFalse(roleId);
        Resource resource = resourceRepository.findByIdAndDeletedFalse(resourceId);
        List<Action> givenPermissions =
                permissionRepository.findByAppRoleIdAndResourceIdAndDeletedFalse(appRole.getId(), resource.getId())
                .stream().map(AppPermission::getAction).toList();
        List<Action> allPermissions = new ArrayList<>(resource.getActions().stream().toList());
        allPermissions.removeAll(givenPermissions);
        return actionMapper.toDTOList(allPermissions);
    }

    public UserPermissionDto updateUserPermissions(UserPermissionDto dto) {
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
        List<AppPermission> updated = permissionRepository.findByAppUserIdAndDeletedFalse(userId);
        return permissionAggregator.toUserPermissionDto(dto.getUserId(), updated);
    }

    public UserPermissionDto updateRolePermissions(RolePermissionDto dto) {
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
        List<AppPermission> updated = permissionRepository.findByAppRoleIdAndDeletedFalse(roleId);
        return permissionAggregator.toUserPermissionDto(roleId, updated);
    }
}

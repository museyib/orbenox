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

    public UserPermissionDto getUserPermission(Long userId) {
        AppUser appUser = userRepository.findByIdAndDeleted(userId, false);
        List<AppPermission> direct = permissionRepository.findByAppUserAndDeleted(appUser, false);
        List<AppPermission> viaRoles = appUser.getRoles()
                .stream()
                .flatMap(role -> permissionRepository.findByAppRoleAndDeleted(role, false).stream())
                .toList();
        List<AppPermission> permissions = Stream.concat(direct.stream(), viaRoles.stream()).toList();
        return permissionAggregator.toUserPermissionDto(appUser.getId(), permissions);
    }

    public RolePermissionDto getRolePermission(Long roleId) {
        AppRole appRole = roleRepository.findByIdAndDeleted(roleId, false);
        List<AppPermission> permissions = permissionRepository.findByAppRoleAndDeleted(appRole, false);
        return permissionAggregator.toRolePermissionDto(appRole.getId(), permissions);
    }

    public List<ActionDto> getGrantablePermissionsForUser(Long userId, Long resourceId) {
        AppUser appUser = userRepository.findByIdAndDeleted(userId, false);
        Resource resource = resourceRepository.findByIdAndDeleted(resourceId, false);
        List<Action> givenPermissions = permissionRepository.findByAppUserAndResourceAndDeleted(appUser, resource, false)
                .stream().map(AppPermission::getAction).toList();
        List<Action> allPermissions = new ArrayList<>(resource.getActions().stream().toList());
        allPermissions.removeAll(givenPermissions);
        return actionMapper.toDTOList(allPermissions);
    }

    public List<ActionDto> getGrantablePermissionsForRole(Long roleId, Long resourceId) {
        AppRole appRole = roleRepository.findByIdAndDeleted(roleId, false);
        Resource resource = resourceRepository.findByIdAndDeleted(resourceId, false);
        List<Action> givenPermissions = permissionRepository.findByAppRoleAndResourceAndDeleted(appRole, resource, false)
                .stream().map(AppPermission::getAction).toList();
        List<Action> allPermissions = new ArrayList<>(resource.getActions().stream().toList());
        allPermissions.removeAll(givenPermissions);
        return actionMapper.toDTOList(allPermissions);
    }

    public UserPermissionDto updateUserPermissions(UserPermissionDto dto) {
        Long userId = dto.getUserId();
        List<AppPermission> existing = permissionRepository.findByAppUserIdAndDeleted(userId, false);

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
                    appPermission.setAppUser(userRepository.findByIdAndDeleted(userId, false));
                    appPermission.setResource(resourceRepository.findByIdAndDeleted(p.resource().id(), false));
                    appPermission.setAction(actionRepository.findByIdAndDeleted(p.action().id(), false));
                    return appPermission;
                }).collect(Collectors.toList());
        if (!appPermissions.isEmpty()) {
            permissionRepository.saveAll(appPermissions);
        }
        List<AppPermission> updated = permissionRepository.findByAppUserIdAndDeleted(userId, false);
        return permissionAggregator.toUserPermissionDto(dto.getUserId(), updated);
    }

    public UserPermissionDto updateRolePermissions(RolePermissionDto dto) {
        Long roleId = dto.getRoleId();
        List<AppPermission> existing = permissionRepository.findByAppRoleIdAndDeleted(roleId, false);

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
                    appPermission.setAppRole(roleRepository.findByIdAndDeleted(roleId, false));
                    appPermission.setResource(resourceRepository.findByIdAndDeleted(p.resource().id(), false));
                    appPermission.setAction(actionRepository.findByIdAndDeleted(p.action().id(), false));
                    return appPermission;
                }).collect(Collectors.toList());
        if (!appPermissions.isEmpty()) {
            permissionRepository.saveAll(appPermissions);
        }
        List<AppPermission> updated = permissionRepository.findByAppRoleIdAndDeleted(roleId, false);
        return permissionAggregator.toUserPermissionDto(roleId, updated);
    }
}

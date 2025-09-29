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
        AppUser appUser = userRepository.findById(userId).orElseThrow();
        List<AppPermission> direct = permissionRepository.findByAppUser(appUser);
        List<AppPermission> viaRoles = appUser.getRoles()
                .stream()
                .flatMap(role -> permissionRepository.findByAppRole(role).stream())
                .toList();
        List<AppPermission> permissions = Stream.concat(direct.stream(), viaRoles.stream()).toList();
        return permissionAggregator.toUserPermissionDto(appUser.getId(), permissions);
    }

    public RolePermissionDto getRolePermission(Long roleId) {
        AppRole appRole = roleRepository.findById(roleId).orElseThrow();
        List<AppPermission> permissions = permissionRepository.findByAppRole(appRole);
        return permissionAggregator.toRolePermissionDto(appRole.getId(), permissions);
    }

    public List<ActionDto> getGrantablePermissionsForUser(Long userId, Long resourceId) {
        AppUser appUser = userRepository.findById(userId).orElseThrow();
        Resource resource = resourceRepository.findById(resourceId).orElseThrow();
        List<Action> givenPermissions = permissionRepository.findByAppUserAndResource(appUser, resource).stream().map(AppPermission::getAction).toList();
        List<Action> allPermissions = new java.util.ArrayList<>(resource.getActions().stream().toList());
        allPermissions.removeAll(givenPermissions);
        return actionMapper.toDTOList(allPermissions);
    }

    public UserPermissionDto updateUserPermissions(UserPermissionDto dto) {
        Long userId = dto.getUserId();
        List<AppPermission> existing = permissionRepository.findByAppUserId(userId);

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
                    appPermission.setAppUser(userRepository.findById(userId).orElseThrow());
                    appPermission.setResource(resourceRepository.findById(p.getResource().getId()).orElseThrow());
                    appPermission.setAction(actionRepository.findById(p.getAction().getId()).orElseThrow());
                    return appPermission;
                }).collect(Collectors.toList());
        if (!appPermissions.isEmpty()) {
            permissionRepository.saveAll(appPermissions);
        }
        List<AppPermission> updated = permissionRepository.findByAppUserId(userId);
        return permissionAggregator.toUserPermissionDto(dto.getUserId(), updated);
    }
}

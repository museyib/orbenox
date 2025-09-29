package com.orbenox.erp.security.service;

import com.orbenox.erp.security.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final PermissionService permissionService;
    private final UserService userService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            AppUser appUser = userService.findByUsername(userDetails.getUsername());
            String permissionCode = targetDomainObject +  ":" + permission;
            boolean isAdmin = appUser.getUserType().name().equals("ADMIN");

            return isAdmin || permissionService.getUserPermission(appUser.getId())
                    .getPermissions()
                    .stream()
                    .anyMatch(appPermission -> appPermission.getPermissionCode().equals(permissionCode));
        } else
            return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            AppUser appUser = userService.findByUsername(userDetails.getUsername());
            String permissionCode = targetType + ":" + permission;
            boolean isAdmin = appUser.getUserType().name().equals("ADMIN");

            return isAdmin || permissionService.getUserPermission(appUser.getId())
                    .getPermissions()
                    .stream()
                    .anyMatch(appPermission -> appPermission.getPermissionCode().equals(permissionCode));
        } else {
            return false;
        }
    }
}

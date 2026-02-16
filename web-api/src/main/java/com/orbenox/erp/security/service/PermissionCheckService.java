package com.orbenox.erp.security.service;

import com.orbenox.erp.security.projection.UserItem;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import static com.orbenox.erp.config.CacheConfig.CacheNames.HAS_PERMISSION;

@Service
@RequiredArgsConstructor
public class PermissionCheckService {
    private final PermissionService permissionService;
    private final UserService userService;

    @Cacheable(HAS_PERMISSION)
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            UserItem appUser = userService.getByUsername(userDetails.getUsername());
            String permissionCode = targetDomainObject + ":" + permission;
            boolean isAdmin = appUser.getUserType().getCode().equals("ADMIN");

            return isAdmin || permissionService.getUserPermission(appUser.getId())
                    .getPermissions()
                    .stream()
                    .anyMatch(appPermission -> appPermission.getPermissionCode().equals(permissionCode));
        } else
            return false;
    }
}


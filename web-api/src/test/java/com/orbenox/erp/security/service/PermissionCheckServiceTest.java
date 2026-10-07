package com.orbenox.erp.security.service;

import com.orbenox.erp.security.projection.PermissionItem;
import com.orbenox.erp.security.projection.UserItem;
import com.orbenox.erp.security.projection.UserTypeItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PermissionCheckServiceTest {
    private PermissionService permissionService;
    private UserService userService;
    private PermissionCheckService permissionCheckService;

    private static Authentication authentication(String username) {
        return new UsernamePasswordAuthenticationToken(
                User.withUsername(username).password("secret").roles("USER").build(), null);
    }

    private static UserItem user(String username, String type, long id) {
        UserItem user = mock(UserItem.class);
        UserTypeItem userType = mock(UserTypeItem.class);
        when(user.getUsername()).thenReturn(username);
        when(user.getId()).thenReturn(id);
        when(user.getUserType()).thenReturn(userType);
        when(userType.getCode()).thenReturn(type);
        return user;
    }

    private static PermissionItem permission(String code) {
        PermissionItem permission = mock(PermissionItem.class);
        when(permission.getPermissionCode()).thenReturn(code);
        return permission;
    }

    @BeforeEach
    void setUp() {
        permissionService = mock(PermissionService.class);
        userService = mock(UserService.class);
        permissionCheckService = new PermissionCheckService(permissionService, userService);
    }

    @Test
    void hasPermission_shouldRejectNonUserDetailsPrincipal() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("alice", null);

        assertThat(permissionCheckService.hasPermission(authentication, "INVOICE", "READ")).isFalse();
        verifyNoInteractions(userService, permissionService);
    }

    @Test
    void hasPermission_shouldGrantAdminsWithoutLoadingPermissionList() {
        UserItem appUser = user("alice", "ADMIN", 7L);
        when(userService.getByUsername("alice")).thenReturn(appUser);

        assertThat(permissionCheckService.hasPermission(authentication("alice"), "INVOICE", "DELETE")).isTrue();
        verifyNoInteractions(permissionService);
    }

    @Test
    void hasPermission_shouldCheckTheResourceActionCodeForNonAdmins() {
        UserItem appUser = user("alice", "USER", 7L);
        PermissionItem matchingPermission = permission("INVOICE:READ");
        when(userService.getByUsername("alice")).thenReturn(appUser);
        var userPermissions = new com.orbenox.erp.security.projection.UserPermissionData();
        userPermissions.setPermissions(List.of(permission("CUSTOMER:READ"), matchingPermission));
        when(permissionService.getUserPermission(7L)).thenReturn(userPermissions);

        assertThat(permissionCheckService.hasPermission(authentication("alice"), "INVOICE", "READ")).isTrue();
        assertThat(permissionCheckService.hasPermission(authentication("alice"), "INVOICE", "UPDATE")).isFalse();
        verify(permissionService, times(2)).getUserPermission(7L);
    }
}

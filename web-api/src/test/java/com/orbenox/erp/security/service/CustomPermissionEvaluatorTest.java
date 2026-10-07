package com.orbenox.erp.security.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomPermissionEvaluatorTest {
    @Test
    void hasPermission_shouldDelegateObjectAndIdentifierFormsToPermissionCheckService() {
        PermissionCheckService permissionCheckService = mock(PermissionCheckService.class);
        CustomPermissionEvaluator evaluator = new CustomPermissionEvaluator(permissionCheckService);
        var authentication = new UsernamePasswordAuthenticationToken("alice", null);
        when(permissionCheckService.hasPermission(authentication, "INVOICE", "READ")).thenReturn(true);

        assertThat(evaluator.hasPermission(authentication, "INVOICE", "READ")).isTrue();
        assertThat(evaluator.hasPermission(authentication, 42L, "INVOICE", "READ")).isTrue();

        verify(permissionCheckService, times(2)).hasPermission(authentication, "INVOICE", "READ");
    }
}

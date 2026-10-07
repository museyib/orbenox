package com.orbenox.erp.security.controller;

import com.orbenox.erp.security.projection.RolePermissionData;
import com.orbenox.erp.security.projection.UserPermissionData;
import com.orbenox.erp.security.request.UpdateRolePermissionRequest;
import com.orbenox.erp.security.request.UpdateUserPermissionRequest;
import com.orbenox.erp.security.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PermissionControllerTest {
    private PermissionService permissionService;
    private PermissionController controller;

    private static <T> void assertSuccess(
            org.springframework.http.ResponseEntity<com.orbenox.erp.common.Response<T>> response,
            T expectedData) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).isSameAs(expectedData);
    }

    @BeforeEach
    void setUp() {
        permissionService = mock(PermissionService.class);
        controller = new PermissionController(permissionService);
    }

    @Test
    void findByUserId_shouldReturnTheDirectPermissionView() {
        UserPermissionData data = new UserPermissionData();
        when(permissionService.getDirectUserPermission(4L)).thenReturn(data);

        var response = controller.findByUserId(4L);

        assertSuccess(response, data);
        verify(permissionService).getDirectUserPermission(4L);
    }

    @Test
    void getAvailableActionsForUser_shouldDelegateBothIdentifiers() {
        List<String> actions = List.of("READ", "UPDATE");
        when(permissionService.getAvailableActionsForUser(4L, 9L)).thenReturn(actions);

        var response = controller.getAvailableActionsForUser(4L, 9L);

        assertSuccess(response, actions);
        verify(permissionService).getAvailableActionsForUser(4L, 9L);
    }

    @Test
    void updateUserPermissions_shouldPassTheRequestToService() {
        UpdateUserPermissionRequest request = new UpdateUserPermissionRequest();
        UserPermissionData result = new UserPermissionData();
        when(permissionService.updateUserPermissions(request)).thenReturn(result);

        var response = controller.updateUserPermissions(request);

        assertSuccess(response, result);
        verify(permissionService).updateUserPermissions(request);
    }

    @Test
    void findByRoleId_shouldReturnRolePermissions() {
        RolePermissionData data = new RolePermissionData();
        when(permissionService.getRolePermission(7L)).thenReturn(data);

        var response = controller.findByRoleId(7L);

        assertSuccess(response, data);
        verify(permissionService).getRolePermission(7L);
    }

    @Test
    void getAvailableActionsForRole_shouldDelegateBothIdentifiers() {
        List<String> actions = List.of("CREATE");
        when(permissionService.getAvailableActionsForRole(7L, 9L)).thenReturn(actions);

        var response = controller.getAvailableActionsForRole(7L, 9L);

        assertSuccess(response, actions);
        verify(permissionService).getAvailableActionsForRole(7L, 9L);
    }

    @Test
    void updateRolePermissions_shouldPassRequestToService() {
        UpdateRolePermissionRequest request = new UpdateRolePermissionRequest();
        RolePermissionData result = new RolePermissionData();
        when(permissionService.updateRolePermissions(request)).thenReturn(result);

        var response = controller.updateRolePermissions(request);

        assertSuccess(response, result);
        verify(permissionService).updateRolePermissions(request);
    }
}

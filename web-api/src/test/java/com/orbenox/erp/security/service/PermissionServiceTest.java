package com.orbenox.erp.security.service;

import com.orbenox.erp.domain.resource.ResourceRepository;
import com.orbenox.erp.security.mapper.RolePermissionMapper;
import com.orbenox.erp.security.mapper.UserPermissionMapper;
import com.orbenox.erp.security.projection.*;
import com.orbenox.erp.security.repository.PermissionRepository;
import com.orbenox.erp.security.repository.RoleRepository;
import com.orbenox.erp.security.repository.UserRepository;
import com.orbenox.erp.security.request.UpdateRolePermissionRequest;
import com.orbenox.erp.security.request.UpdateUserPermissionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PermissionServiceTest {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private ResourceRepository resourceRepository;
    private PermissionRepository permissionRepository;
    private UserPermissionMapper userPermissionMapper;
    private RolePermissionMapper rolePermissionMapper;
    private PermissionService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        resourceRepository = mock(ResourceRepository.class);
        permissionRepository = mock(PermissionRepository.class);
        userPermissionMapper = mock(UserPermissionMapper.class);
        rolePermissionMapper = mock(RolePermissionMapper.class);
        service = new PermissionService(userRepository, roleRepository, resourceRepository,
                permissionRepository, userPermissionMapper, rolePermissionMapper);
    }

    @Test
    void getDirectUserPermission_shouldCombineUserAndDirectPermissions() {
        SimpleUserItem user = mock(SimpleUserItem.class);
        PermissionItem permission = mock(PermissionItem.class);
        when(userRepository.getItemById(3L)).thenReturn(user);
        when(permissionRepository.getPermissionsByUserId(3L)).thenReturn(List.of(permission));

        UserPermissionData result = service.getDirectUserPermission(3L);

        assertThat(result.getUser()).isSameAs(user);
        assertThat(result.getPermissions()).containsExactly(permission);
    }

    @Test
    void getUserPermission_shouldMergeDirectAndRolePermissions() {
        RoleItem role = mock(RoleItem.class);
        PermissionItem direct = mock(PermissionItem.class);
        PermissionItem inherited = mock(PermissionItem.class);
        SimpleUserItem user = mock(SimpleUserItem.class);
        when(role.getId()).thenReturn(8L);
        when(userRepository.getRolesByUserId(3L)).thenReturn(List.of(role));
        when(userRepository.getItemById(3L)).thenReturn(user);
        when(permissionRepository.getPermissionsByUserId(3L)).thenReturn(List.of(direct));
        when(permissionRepository.getPermissionsByRoleId(8L)).thenReturn(List.of(inherited));

        UserPermissionData result = service.getUserPermission(3L);

        assertThat(result.getUser()).isSameAs(user);
        assertThat(result.getPermissions()).containsExactly(direct, inherited);
    }

    @Test
    void getRolePermission_shouldReturnRoleAndAssignedPermissions() {
        RoleItem role = mock(RoleItem.class);
        PermissionItem permission = mock(PermissionItem.class);
        when(roleRepository.getItemById(8L)).thenReturn(role);
        when(permissionRepository.getPermissionsByRoleId(8L)).thenReturn(List.of(permission));

        RolePermissionData result = service.getRolePermission(8L);

        assertThat(result.getRole()).isSameAs(role);
        assertThat(result.getPermissions()).containsExactly(permission);
    }

    @Test
    void getAvailableActions_shouldRemoveAlreadyAssignedActionsForUsersAndRoles() {
        when(permissionRepository.getActionItemsByAppUserIdAndResourceId(3L, 5L))
                .thenReturn(List.of("READ"));
        when(permissionRepository.getActionItemsByAppRoleIdAndResourceId(8L, 5L))
                .thenReturn(List.of("UPDATE"));
        when(resourceRepository.getActionItemsByResourceId(5L))
                .thenReturn(new ArrayList<>(List.of("READ", "UPDATE", "DELETE")),
                        new ArrayList<>(List.of("READ", "UPDATE", "DELETE")));

        assertThat(service.getAvailableActionsForUser(3L, 5L)).containsExactly("UPDATE", "DELETE");
        assertThat(service.getAvailableActionsForRole(8L, 5L)).containsExactly("READ", "DELETE");
    }

    @Test
    void updateUserPermissions_shouldSaveAndDeleteBeforeReturningUpdatedView() {
        UpdateUserPermissionRequest request = new UpdateUserPermissionRequest();
        request.setUserId(3L);
        request.setPermissionsToDelete(List.of());
        request.setPermissionsToInsert(List.of());
        when(userRepository.getRolesByUserId(3L)).thenReturn(List.of());
        when(permissionRepository.getPermissionsByUserId(3L)).thenReturn(List.of());

        UserPermissionData result = service.updateUserPermissions(request);

        assertThat(result.getPermissions()).isEmpty();
        verify(permissionRepository).saveAll(List.of());
        verify(permissionRepository).deleteAllById(List.of());
    }

    @Test
    void updateRolePermissions_shouldSaveAndDeleteBeforeReturningUpdatedView() {
        UpdateRolePermissionRequest request = new UpdateRolePermissionRequest();
        request.setRoleId(8L);
        request.setPermissionsToDelete(List.of());
        request.setPermissionsToInsert(List.of());
        RoleItem role = mock(RoleItem.class);
        when(roleRepository.getItemById(8L)).thenReturn(role);
        when(permissionRepository.getPermissionsByRoleId(8L)).thenReturn(List.of());

        RolePermissionData result = service.updateRolePermissions(request);

        assertThat(result.getRole()).isSameAs(role);
        assertThat(result.getPermissions()).isEmpty();
        verify(permissionRepository).saveAll(List.of());
        verify(permissionRepository).deleteAllById(List.of());
    }
}

package com.orbenox.erp.security.controller;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.security.dto.RoleCreateDto;
import com.orbenox.erp.security.dto.RoleUpdateDto;
import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RoleControllerTest {
    private RoleService roleService;
    private LocalizationService localizationService;
    private RoleController controller;

    @BeforeEach
    void setUp() {
        roleService = mock(RoleService.class);
        localizationService = mock(LocalizationService.class);
        controller = new RoleController(roleService, localizationService);
    }

    @Test
    void getAll_shouldReturnPageContentAndNavigationHeaders() {
        Slice<RoleItem> slice = mock(Slice.class);
        List<RoleItem> content = List.of(mock(RoleItem.class));
        when(roleService.getAllItems(2, 5, "finance")).thenReturn(slice);
        when(slice.getContent()).thenReturn(content);
        when(slice.hasNext()).thenReturn(true);
        when(slice.hasPrevious()).thenReturn(false);

        var response = controller.getAll(2, 5, "finance");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isSameAs(content);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", true)
                .containsEntry("hasPrev", false);
        verify(roleService).getAllItems(2, 5, "finance");
    }

    @Test
    void getById_shouldReturnTheRequestedRole() {
        RoleItem role = mock(RoleItem.class);
        when(roleService.getItemById(3L)).thenReturn(role);

        var response = controller.getById(3L);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isSameAs(role);
        verify(roleService).getItemById(3L);
    }

    @Test
    void create_shouldDelegateDtoAndReturnCreatedRoleData() {
        RoleCreateDto dto = new RoleCreateDto(true, "FINANCE", "Finance");
        RoleItem role = mock(RoleItem.class);
        when(roleService.create(dto)).thenReturn(role);

        var response = controller.create(dto);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isSameAs(role);
        verify(roleService).create(dto);
    }

    @Test
    void update_shouldDelegateIdAndDtoAndReturnUpdatedRole() {
        RoleUpdateDto dto = new RoleUpdateDto(3L, true, "FINANCE", "Finance");
        RoleItem role = mock(RoleItem.class);
        when(roleService.update(3L, dto)).thenReturn(role);

        var response = controller.updateRole(3L, dto);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isSameAs(role);
        verify(roleService).update(3L, dto);
    }

    @Test
    void delete_shouldSoftDeleteAndReturnLocalizedSuccessMessage() {
        when(localizationService.msg("role.deleted", 3L)).thenReturn("Role deleted");

        var response = controller.delete(3L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Role deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("role.deleted");
        verify(roleService).softDelete(3L);
        verify(localizationService).msg("role.deleted", 3L);
    }
}

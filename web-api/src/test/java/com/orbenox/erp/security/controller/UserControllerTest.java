package com.orbenox.erp.security.controller;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.security.dto.UserCreateDto;
import com.orbenox.erp.security.dto.UserUpdateDto;
import com.orbenox.erp.security.projection.SimpleUserItem;
import com.orbenox.erp.security.projection.UserData;
import com.orbenox.erp.security.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {
    private UserService userService;
    private LocalizationService localizationService;
    private UserController controller;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        localizationService = mock(LocalizationService.class);
        controller = new UserController(userService, localizationService);
    }

    @Test
    void getAllUsers_shouldReturnPageContentAndNavigationHeaders() {
        Slice<SimpleUserItem> slice = mock(Slice.class);
        List<SimpleUserItem> content = List.of(mock(SimpleUserItem.class));
        when(userService.getAllItems(1, 8, "alice")).thenReturn(slice);
        when(slice.getContent()).thenReturn(content);
        when(slice.hasNext()).thenReturn(false);
        when(slice.hasPrevious()).thenReturn(true);

        var response = controller.getAllUsers(1, 8, "alice");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isSameAs(content);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", false)
                .containsEntry("hasPrev", true);
        verify(userService).getAllItems(1, 8, "alice");
    }

    @Test
    void getById_shouldReturnUserData() {
        UserData data = new UserData();
        when(userService.getItemById(6L)).thenReturn(data);

        var response = controller.getById(6L);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isSameAs(data);
        verify(userService).getItemById(6L);
    }

    @Test
    void create_shouldDelegateDtoAndReturnUserProjection() {
        UserCreateDto dto = new UserCreateDto(true, "alice", "secret", "Alice", 2L, Set.of(4L));
        SimpleUserItem user = mock(SimpleUserItem.class);
        when(userService.create(dto)).thenReturn(user);

        var response = controller.create(dto);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isSameAs(user);
        verify(userService).create(dto);
    }

    @Test
    void update_shouldDelegateIdAndDtoAndReturnUpdatedUser() {
        UserUpdateDto dto = new UserUpdateDto(6L, true, "alice", "secret", "Alice", 2L, Set.of());
        SimpleUserItem user = mock(SimpleUserItem.class);
        when(userService.update(6L, dto)).thenReturn(user);

        var response = controller.update(6L, dto);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isSameAs(user);
        verify(userService).update(6L, dto);
    }

    @Test
    void delete_shouldSoftDeleteAndReturnLocalizedSuccessMessage() {
        when(localizationService.msg("user.deleted", 6L)).thenReturn("User deleted");

        var response = controller.delete(6L);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("User deleted");
        assertThat(response.getMessageKey()).isEqualTo("user.deleted");
        verify(userService).delete(6L);
        verify(localizationService).msg("user.deleted", 6L);
    }
}

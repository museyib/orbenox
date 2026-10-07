package com.orbenox.erp.security.service;

import com.orbenox.erp.exception.AlterRootException;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.security.dto.UserCreateDto;
import com.orbenox.erp.security.dto.UserUpdateDto;
import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.entity.UserType;
import com.orbenox.erp.security.mapper.UserMapper;
import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.projection.SimpleUserItem;
import com.orbenox.erp.security.projection.UserData;
import com.orbenox.erp.security.projection.UserItem;
import com.orbenox.erp.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserRepository repository;
    private PasswordEncoder passwordEncoder;
    private UserMapper mapper;
    private LocalizationService localizationService;
    private UserService service;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        mapper = mock(UserMapper.class);
        localizationService = mock(LocalizationService.class);
        service = new UserService(repository, passwordEncoder, mapper, localizationService);
    }

    @Test
    void getAllItems_shouldUsePagedListingOrSearchBasedOnSearchText() {
        Slice<SimpleUserItem> all = mock(Slice.class);
        Slice<SimpleUserItem> found = mock(Slice.class);
        when(repository.getAllItems(PageRequest.of(1, 10))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(1, 10), "alice")).thenReturn(found);

        assertThat(service.getAllItems(1, 10, "   ")).isSameAs(all);
        assertThat(service.getAllItems(1, 10, "alice")).isSameAs(found);
        verify(repository, never()).getItemsSearched(any(), eq("   "));
    }

    @Test
    void getItemByIdAndGetByUsername_shouldReturnRepositoryData() {
        SimpleUserItem user = mock(SimpleUserItem.class);
        RoleItem role = mock(RoleItem.class);
        UserItem userDetails = mock(UserItem.class);
        when(repository.getItemById(9L)).thenReturn(user);
        when(repository.getRolesByUserId(9L)).thenReturn(List.of(role));
        when(repository.getItemByUsername("alice")).thenReturn(userDetails);

        UserData data = service.getItemById(9L);

        assertThat(data.getUser()).isSameAs(user);
        assertThat(data.getRoles()).containsExactly(role);
        assertThat(service.getByUsername("alice")).isSameAs(userDetails);
    }

    @Test
    void create_shouldEncodePasswordBeforeSavingAndReturnTheSavedProjection() {
        UserCreateDto dto = new UserCreateDto(true, "alice", "plain", "Alice", 4L, Set.of());
        AppUser mapped = new AppUser();
        mapped.setPassword("plain");
        AppUser saved = new AppUser();
        saved.setId(12L);
        SimpleUserItem result = mock(SimpleUserItem.class);
        when(mapper.toEntity(dto)).thenReturn(mapped);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(repository.save(mapped)).thenReturn(saved);
        when(repository.getItemById(12L)).thenReturn(result);

        assertThat(service.create(dto)).isSameAs(result);
        assertThat(mapped.getPassword()).isEqualTo("encoded");
        verify(repository).save(mapped);
    }

    @Test
    void update_shouldRejectChangingTheRootAdministrator() {
        AppUser root = new AppUser();
        root.setRoot(true);
        UserType adminType = new UserType();
        adminType.setCode("ADMIN");
        root.setUserType(adminType);
        when(repository.findByIdAndDeletedFalse(1L)).thenReturn(root);
        when(localizationService.msg("error.alterRoot")).thenReturn("root cannot be changed");
        UserUpdateDto dto = new UserUpdateDto(1L, true, "admin", "x", "Admin", 1L, Set.of());

        assertThatThrownBy(() -> service.update(1L, dto))
                .isInstanceOf(AlterRootException.class)
                .hasMessage("root cannot be changed");
        verify(repository, never()).getItemById(1L);
    }

    @Test
    void updateAndDelete_shouldPersistNonRootChangesAndProtectRootUsername() {
        AppUser regularUser = new AppUser();
        regularUser.setUsername("alice");
        SimpleUserItem projection = mock(SimpleUserItem.class);
        when(repository.findByIdAndDeletedFalse(2L)).thenReturn(regularUser);
        when(repository.getItemById(2L)).thenReturn(projection);
        UserUpdateDto dto = new UserUpdateDto(2L, true, "alice", "pw", "Alice", 3L, Set.of());

        assertThat(service.update(2L, dto)).isSameAs(projection);
        service.delete(2L);
        assertThat(regularUser.isDeleted()).isTrue();

        AppUser root = new AppUser();
        root.setUsername("admin");
        when(repository.findByIdAndDeletedFalse(1L)).thenReturn(root);
        when(localizationService.msg("error.deleteRoot")).thenReturn("root cannot be deleted");
        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(AlterRootException.class)
                .hasMessage("root cannot be deleted");
    }
}

package com.orbenox.erp.security.mapper;

import com.orbenox.erp.security.dto.UserCreateDto;
import com.orbenox.erp.security.dto.UserUpdateDto;
import com.orbenox.erp.security.entity.AppRole;
import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.entity.UserType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserMapperTest {
    private EntityManager entityManager;
    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        mapper = Mappers.getMapper(UserMapper.class);
        ReflectionTestUtils.setField(mapper, "entityManager", entityManager);
    }

    @Test
    void toEntity_shouldMapUserFieldsAndResolveUserTypeAndRoles() {
        UserType userType = new UserType();
        AppRole role = new AppRole();
        role.setId(6L);
        AppRole secondRole = new AppRole();
        secondRole.setId(7L);
        when(entityManager.getReference(UserType.class, 5L)).thenReturn(userType);
        when(entityManager.getReference(AppRole.class, 6L)).thenReturn(role);
        when(entityManager.getReference(AppRole.class, 7L)).thenReturn(secondRole);

        AppUser result = mapper.toEntity(new UserCreateDto(
                false, "alice", "encoded", "Alice", 5L, Set.of(6L, 7L)));

        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getPassword()).isEqualTo("encoded");
        assertThat(result.getDisplayName()).isEqualTo("Alice");
        assertThat(result.isEnabled()).isFalse();
        assertThat(result.getUserType()).isSameAs(userType);
        assertThat(result.getRoles()).extracting(AppRole::getId).containsExactlyInAnyOrder(6L, 7L);
        verify(entityManager).getReference(UserType.class, 5L);
        verify(entityManager).getReference(AppRole.class, 6L);
        verify(entityManager).getReference(AppRole.class, 7L);
    }

    @Test
    void toEntity_shouldMapZeroUserTypeAndIgnoreNullRoleIds() {
        AppUser result = mapper.toEntity(new UserCreateDto(
                true, "bob", "secret", "Bob", 0L, null));

        assertThat(result.getUserType()).isNull();
        assertThat(result.getRoles()).isNull();
        verifyNoInteractions(entityManager);
    }

    @Test
    void toEntity_shouldSkipNullRoleIdsAndMapZeroRoleIdsToNull() {
        AppRole role = new AppRole();
        role.setId(6L);
        when(entityManager.getReference(AppRole.class, 6L)).thenReturn(role);

        AppUser result = mapper.toEntity(new UserCreateDto(
                true, "carol", "secret", "Carol", 0L, new HashSet<>(java.util.Arrays.asList(6L, 0L, null))));

        assertThat(result.getRoles()).hasSize(2).contains(role).containsNull();
        verify(entityManager).getReference(AppRole.class, 6L);
        verifyNoMoreInteractions(entityManager);
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFieldsAndKeepExistingNullIgnoredValues() {
        UserType newType = new UserType();
        AppRole role = new AppRole();
        when(entityManager.getReference(UserType.class, 8L)).thenReturn(newType);
        when(entityManager.getReference(AppRole.class, 9L)).thenReturn(role);
        AppUser user = new AppUser();
        user.setUsername("old");
        user.setPassword("old-password");
        user.setDisplayName("Old name");
        UserType originalType = new UserType();
        user.setUserType(originalType);
        Set<AppRole> originalRoles = new HashSet<>(Set.of(new AppRole()));
        user.setRoles(originalRoles);

        mapper.updateEntityFromDto(
                new UserUpdateDto(10L, false, "new", null, null, 8L, Set.of(9L)), user);

        assertThat(user.getUsername()).isEqualTo("new");
        assertThat(user.getPassword()).isEqualTo("old-password");
        assertThat(user.getDisplayName()).isEqualTo("Old name");
        assertThat(user.isEnabled()).isFalse();
        assertThat(user.getUserType()).isSameAs(newType);
        assertThat(user.getRoles()).containsExactly(role);
    }

    @Test
    void updateEntityFromDto_shouldPreserveRelationshipsWhenTheirIdsAreNull() {
        UserType originalType = new UserType();
        Set<AppRole> originalRoles = Set.of(new AppRole());
        AppUser user = new AppUser();
        user.setUserType(originalType);
        user.setRoles(originalRoles);

        mapper.updateEntityFromDto(new UserUpdateDto(10L, true, null, null, null, null, null), user);

        assertThat(user.getUserType()).isSameAs(originalType);
        assertThat(user.getRoles()).isSameAs(originalRoles);
        verifyNoInteractions(entityManager);
    }

    @Test
    void mapperMethods_shouldHandleNullDtoInputs() {
        AppUser user = new AppUser();
        user.setUsername("unchanged");

        assertThat(mapper.toEntity(null)).isNull();
        mapper.updateEntityFromDto(null, user);

        assertThat(user.getUsername()).isEqualTo("unchanged");
        verifyNoInteractions(entityManager);
    }
}

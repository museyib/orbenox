package com.orbenox.erp.security.mapper;

import com.orbenox.erp.domain.resource.Resource;
import com.orbenox.erp.enums.Action;
import com.orbenox.erp.security.dto.UserPermissionCreateDto;
import com.orbenox.erp.security.entity.AppPermission;
import com.orbenox.erp.security.entity.AppUser;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserPermissionMapperTest {
    private EntityManager entityManager;
    private UserPermissionMapper mapper;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        mapper = Mappers.getMapper(UserPermissionMapper.class);
        ReflectionTestUtils.setField(mapper, "entityManager", entityManager);
    }

    @Test
    void toEntity_shouldResolveUserAndResourceAndMapAction() {
        AppUser user = new AppUser();
        Resource resource = new Resource();
        when(entityManager.getReference(AppUser.class, 4L)).thenReturn(user);
        when(entityManager.getReference(Resource.class, 8L)).thenReturn(resource);

        AppPermission result = mapper.toEntity(new UserPermissionCreateDto(false, 4L, 8L, "UPDATE"));

        assertThat(result.getAppUser()).isSameAs(user);
        assertThat(result.getResource()).isSameAs(resource);
        assertThat(result.getAction()).isEqualTo(Action.UPDATE);
        assertThat(result.isEnabled()).isTrue();
        verify(entityManager).getReference(AppUser.class, 4L);
        verify(entityManager).getReference(Resource.class, 8L);
    }

    @Test
    void toEntity_shouldMapZeroRelationIdsToNullWithoutLookingUpReferences() {
        AppPermission result = mapper.toEntity(new UserPermissionCreateDto(true, 0L, 0L, "READ"));

        assertThat(result.getAppUser()).isNull();
        assertThat(result.getResource()).isNull();
        assertThat(result.getAction()).isEqualTo(Action.READ);
        verifyNoInteractions(entityManager);
    }

    @Test
    void toEntity_shouldReturnNullWhenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
        verifyNoInteractions(entityManager);
    }
}

package com.orbenox.erp.security.mapper;

import com.orbenox.erp.domain.resource.Resource;
import com.orbenox.erp.enums.Action;
import com.orbenox.erp.security.dto.RolePermissionCreateDto;
import com.orbenox.erp.security.entity.AppPermission;
import com.orbenox.erp.security.entity.AppRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RolePermissionMapperTest {
    private EntityManager entityManager;
    private RolePermissionMapper mapper;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        mapper = Mappers.getMapper(RolePermissionMapper.class);
        ReflectionTestUtils.setField(mapper, "entityManager", entityManager);
    }

    @Test
    void toEntity_shouldResolveRoleAndResourceAndMapAction() {
        AppRole role = new AppRole();
        Resource resource = new Resource();
        when(entityManager.getReference(AppRole.class, 2L)).thenReturn(role);
        when(entityManager.getReference(Resource.class, 5L)).thenReturn(resource);

        AppPermission result = mapper.toEntity(new RolePermissionCreateDto(false, 2L, 5L, "APPROVE"));

        assertThat(result.getAppRole()).isSameAs(role);
        assertThat(result.getResource()).isSameAs(resource);
        assertThat(result.getAction()).isEqualTo(Action.APPROVE);
        assertThat(result.isEnabled()).isTrue();
        verify(entityManager).getReference(AppRole.class, 2L);
        verify(entityManager).getReference(Resource.class, 5L);
    }

    @Test
    void toEntity_shouldMapZeroRelationIdsToNullWithoutLookingUpReferences() {
        AppPermission result = mapper.toEntity(new RolePermissionCreateDto(true, 0L, 0L, "DELETE"));

        assertThat(result.getAppRole()).isNull();
        assertThat(result.getResource()).isNull();
        assertThat(result.getAction()).isEqualTo(Action.DELETE);
        verifyNoInteractions(entityManager);
    }

    @Test
    void toEntity_shouldReturnNullWhenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
        verifyNoInteractions(entityManager);
    }
}

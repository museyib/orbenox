package com.orbenox.erp.security.mapper;

import com.orbenox.erp.domain.resource.Resource;
import com.orbenox.erp.security.dto.RolePermissionCreateDto;
import com.orbenox.erp.security.entity.AppPermission;
import com.orbenox.erp.security.entity.AppRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class RolePermissionMapper {
    @PersistenceContext
    private EntityManager entityManager;

    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "appRole", source = "roleId")
    @Mapping(target = "resource", source = "resourceId")
    public abstract AppPermission toEntity(RolePermissionCreateDto dto);

    AppRole mapToAppRole(Long value) {
        return value == 0 ? null : entityManager.getReference(AppRole.class, value);
    }

    Resource mapToResource(Long value) {
        return value == 0 ? null : entityManager.getReference(Resource.class, value);
    }
}

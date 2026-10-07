package com.orbenox.erp.security.mapper;

import com.orbenox.erp.domain.resource.Resource;
import com.orbenox.erp.security.dto.UserPermissionCreateDto;
import com.orbenox.erp.security.entity.AppPermission;
import com.orbenox.erp.security.entity.AppUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UserPermissionMapper {
    @PersistenceContext
    private EntityManager entityManager;

    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "appUser", source = "userId")
    @Mapping(target = "resource", source = "resourceId")
    public abstract AppPermission toEntity(UserPermissionCreateDto dto);

    AppUser mapToAppUser(Long value) {
        return value == 0 ? null : entityManager.getReference(AppUser.class, value);
    }

    Resource mapToResource(Long value) {
        return value == 0 ? null : entityManager.getReference(Resource.class, value);
    }
}

package com.orbenox.erp.security.mapper;

import com.orbenox.erp.security.dto.UserCreateDto;
import com.orbenox.erp.security.dto.UserUpdateDto;
import com.orbenox.erp.security.entity.AppRole;
import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.entity.UserType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashSet;
import java.util.Set;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @PersistenceContext
    private EntityManager entityManager;

    @Mapping(target = "userType", source = "userTypeId")
    @Mapping(target = "roles", source = "roleIds")
    public abstract AppUser toEntity(UserCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "userType", source = "userTypeId")
    @Mapping(target = "roles", source = "roleIds")
    public abstract void updateEntityFromDto(UserUpdateDto dto, @MappingTarget AppUser entity);

    UserType mapToUserType(Long value) {
        return value == 0 ? null : entityManager.getReference(UserType.class, value);
    }

    AppRole mapToAppRole(Long value) {
        return value == 0 ? null : entityManager.getReference(AppRole.class, value);
    }

    Set<AppRole> mapToRoleSet(Set<Long> values) {
        if (values == null) {
            return null;
        }
        Set<AppRole> roles = new HashSet<>();
        for (Long value : values) {
            if (value != null) {
                roles.add(mapToAppRole(value));
            }
        }
        return roles;
    }
}

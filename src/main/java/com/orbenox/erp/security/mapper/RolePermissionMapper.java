package com.orbenox.erp.security.mapper;

import com.orbenox.erp.security.dto.RolePermissionCreateDto;
import com.orbenox.erp.security.entity.AppPermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RolePermissionMapper {
    @Mapping(target = "enabled", ignore = true)
    AppPermission toEntity(RolePermissionCreateDto dto);
}

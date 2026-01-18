package com.orbenox.erp.security.mapper;

import com.orbenox.erp.security.dto.UserPermissionCreateDto;
import com.orbenox.erp.security.entity.AppPermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPermissionMapper {
    @Mapping(target = "enabled", ignore = true)
    AppPermission toEntity(UserPermissionCreateDto dto);
}

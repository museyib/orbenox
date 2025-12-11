package com.orbenox.erp.security.mapper;

import com.orbenox.erp.common.action.ActionMapper;
import com.orbenox.erp.common.resource.ResourceMapper;
import com.orbenox.erp.security.dto.PermissionDto;
import com.orbenox.erp.security.entity.AppPermission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ResourceMapper.class, ActionMapper.class})
public interface PermissionMapper {
    PermissionDto toDto(AppPermission permission);
    List<PermissionDto> toDtoList(List<AppPermission> permissions);
    AppPermission toEntity(PermissionDto dto);
}

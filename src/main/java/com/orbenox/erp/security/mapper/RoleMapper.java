package com.orbenox.erp.security.mapper;

import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.entity.AppRole;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    AppRole toEntity(RoleDto dto);
    Set<AppRole> toEntityList(List<RoleDto> dtoList);
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDTO(RoleDto dto, @MappingTarget AppRole role);
}

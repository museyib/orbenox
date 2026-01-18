package com.orbenox.erp.security.mapper;

import com.orbenox.erp.security.dto.RoleCreateDto;
import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.entity.AppRole;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Set;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    AppRole toEntity(RoleCreateDto dto);

    Set<AppRole> toEntityList(Set<RoleDto> dtoList);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDTO(RoleDto dto, @MappingTarget AppRole role);
}

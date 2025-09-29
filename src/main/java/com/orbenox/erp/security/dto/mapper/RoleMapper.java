package com.orbenox.erp.security.dto.mapper;

import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.entity.AppRole;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto toDTO(AppRole role);
    List<RoleDto> toDTOList(List<AppRole> roles);
    AppRole toEntity(RoleDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDTO(RoleDto dto, @MappingTarget AppRole role);
}

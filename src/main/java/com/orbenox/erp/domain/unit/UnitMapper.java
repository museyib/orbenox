package com.orbenox.erp.domain.unit;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface UnitMapper {
    Unit toEntity(UnitCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "unitDimension", ignore = true)
    void updateEntityFromDto(UnitDto dto, @MappingTarget Unit entity);
}

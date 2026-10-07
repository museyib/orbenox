package com.orbenox.erp.domain.unit.unitdimension;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface UnitDimensionMapper {
    UnitDimension toEntity(UnitDimensionDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(UnitDimensionDto dto, @MappingTarget UnitDimension entity);
}

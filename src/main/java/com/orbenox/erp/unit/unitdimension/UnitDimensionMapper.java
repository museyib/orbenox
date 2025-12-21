package com.orbenox.erp.unit.unitdimension;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UnitDimensionMapper {
    void updateEntityFromDto(UnitDimensionDto dto, @MappingTarget UnitDimension entity);
}

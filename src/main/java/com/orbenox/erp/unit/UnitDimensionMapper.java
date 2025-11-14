package com.orbenox.erp.unit;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnitDimensionMapper {
    UnitDimensionDto toDto(UnitDimension dto);
    UnitDimension toEntity(UnitDimensionDto entity);
    List<UnitDimensionDto>  toDtoList(List<UnitDimension> entityList);
    void updateEntityFromDto(UnitDimensionDto dto, @MappingTarget UnitDimension entity);
}

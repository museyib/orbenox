package com.orbenox.erp.unit;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = UnitDimensionMapper.class)
public interface UnitMapper {
    UnitDto toDto(Unit unit);
    Unit toEntity(UnitDto unitDto);
    List<UnitDto>  toDtoList(List<Unit> unitList);
    void updateEntityFromDto(UnitDto unitDto, @MappingTarget Unit unit);
}

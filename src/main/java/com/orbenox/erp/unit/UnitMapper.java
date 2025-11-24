package com.orbenox.erp.unit;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = UnitDimensionMapper.class)
public interface UnitMapper {
    UnitDto toDto(Unit entity);
    Unit toEntity(UnitDto dto);
    List<UnitDto>  toDtoList(List<Unit> entityList);
    void updateEntityFromDto(UnitDto dto, @MappingTarget Unit entity);
}

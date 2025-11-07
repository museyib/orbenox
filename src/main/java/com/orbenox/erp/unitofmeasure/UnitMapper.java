package com.orbenox.erp.unitofmeasure;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnitMapper {
    UnitDto toDto(UnitOfMeasure unit);
    UnitOfMeasure toEntity(UnitDto unitDto);
    List<UnitDto>  toDtoList(List<UnitOfMeasure> unitList);
    void updateEntityFromDto(UnitDto unitDto, @MappingTarget UnitOfMeasure unit);
}

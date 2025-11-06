package com.orbenox.erp.unitofmeasure;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UOMMapper {
    UnitOfMeasureDto toDto(UnitOfMeasure uom);
    UnitOfMeasure toEntity(UnitOfMeasureDto uomDto);
    List<UnitOfMeasureDto>  toDtoList(List<UnitOfMeasure> uomList);
    void updateEntityFromDto(UnitOfMeasureDto uomDto,@MappingTarget UnitOfMeasure uom);
}

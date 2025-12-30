package com.orbenox.erp.domain.unit;

import com.orbenox.erp.domain.unit.unitdimension.UnitDimensionMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = UnitDimensionMapper.class)
public interface UnitMapper {
    Unit toEntity(UnitDto dto);

    @Mapping(target = "unitDimension", ignore = true)
    void updateEntityFromDto(UnitDto dto, @MappingTarget Unit entity);
}

package com.orbenox.erp.domain.warehouse;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Warehouse toEntity(WarehouseDto dto);

    void updateEntityFromDto(WarehouseDto dto, @MappingTarget Warehouse entity);
}

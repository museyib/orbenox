package com.orbenox.erp.domain.warehouse;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Warehouse toEntity(WarehouseCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(WarehouseUpdateDto dto, @MappingTarget Warehouse entity);
}

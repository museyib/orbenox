package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductWarehouseDto;
import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductWarehouseMapper {
    ProductWarehouse toEntity(ProductWarehouseDto dto);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    void updateEntityFromDto(ProductWarehouseDto dto, @MappingTarget ProductWarehouse entity);
}

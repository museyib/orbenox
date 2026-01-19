package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductWarehouseCreateDto;
import com.orbenox.erp.domain.product.dto.ProductWarehouseUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ProductWarehouseMapper {
    ProductWarehouse toEntity(ProductWarehouseCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    void updateEntityFromDto(ProductWarehouseUpdateDto dto, @MappingTarget ProductWarehouse entity);
}

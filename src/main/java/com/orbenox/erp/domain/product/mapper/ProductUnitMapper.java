package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductUnitCreateDto;
import com.orbenox.erp.domain.product.dto.ProductUnitDto;
import com.orbenox.erp.domain.product.entity.ProductUnit;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ProductUnitMapper {
    ProductUnit toEntity(ProductUnitCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "unit", ignore = true)
    void updateEntityFromDto(ProductUnitDto dto, @MappingTarget ProductUnit entity);
}

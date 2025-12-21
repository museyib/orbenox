package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductTypeDto;
import com.orbenox.erp.product.entity.ProductType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductTypeMapper {
    ProductType toEntity(ProductTypeDto dto);
    void updateEntityFromDto(ProductTypeDto dto, @MappingTarget ProductType entity);
}

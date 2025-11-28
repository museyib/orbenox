package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductTypeDto;
import com.orbenox.erp.product.entity.ProductType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductTypeMapper {
    ProductTypeDto toDto(ProductType entity);
    ProductType toEntity(ProductTypeDto dto);
    List<ProductTypeDto> toDtoList(List<ProductType> entityList);
    void updateEntityFromDto(ProductTypeDto dto, @MappingTarget ProductType entity);
}

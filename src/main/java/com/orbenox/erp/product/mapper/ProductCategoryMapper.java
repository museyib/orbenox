package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductCategoryDto;
import com.orbenox.erp.product.entity.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {
    ProductCategoryDto toDto(ProductCategory entity);
    ProductCategory toEntity(ProductCategoryDto dto);
    List<ProductCategoryDto> toDtoList(List<ProductCategory> entityList);
    void updateEntityFromDto(ProductCategoryDto dto, @MappingTarget ProductCategory entity);
}

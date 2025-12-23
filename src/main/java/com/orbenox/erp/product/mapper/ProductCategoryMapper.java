package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductCategoryDto;
import com.orbenox.erp.product.entity.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {
    ProductCategory toEntity(ProductCategoryDto dto);

    void updateEntityFromDto(ProductCategoryDto dto, @MappingTarget ProductCategory entity);
}

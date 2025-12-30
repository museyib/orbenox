package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductCategoryDto;
import com.orbenox.erp.domain.product.entity.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {
    ProductCategory toEntity(ProductCategoryDto dto);

    void updateEntityFromDto(ProductCategoryDto dto, @MappingTarget ProductCategory entity);
}

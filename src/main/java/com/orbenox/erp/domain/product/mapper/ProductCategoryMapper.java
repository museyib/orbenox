package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductCategoryCreateDto;
import com.orbenox.erp.domain.product.dto.ProductCategoryDto;
import com.orbenox.erp.domain.product.entity.ProductCategory;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {
    ProductCategory toEntity(ProductCategoryCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(ProductCategoryDto dto, @MappingTarget ProductCategory entity);
}

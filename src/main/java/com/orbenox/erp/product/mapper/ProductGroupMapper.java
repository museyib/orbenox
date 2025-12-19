package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductGroupDto;
import com.orbenox.erp.product.entity.ProductGroup;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductGroupMapper {
    ProductGroup toEntity(ProductGroupDto dto);
    void updateEntityFromDto(ProductGroupDto dto, @MappingTarget ProductGroup entity);
}

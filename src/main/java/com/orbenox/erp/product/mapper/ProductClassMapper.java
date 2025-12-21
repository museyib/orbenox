package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductClassDto;
import com.orbenox.erp.product.entity.ProductClass;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductClassMapper {
    ProductClass toEntity(ProductClassDto dto);
    void updateEntityFromDto(ProductClassDto dto, @MappingTarget ProductClass entity);
}

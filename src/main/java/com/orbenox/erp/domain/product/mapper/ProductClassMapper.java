package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductClassDto;
import com.orbenox.erp.domain.product.entity.ProductClass;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductClassMapper {
    ProductClass toEntity(ProductClassDto dto);

    void updateEntityFromDto(ProductClassDto dto, @MappingTarget ProductClass entity);
}

package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductTypeCreateDto;
import com.orbenox.erp.domain.product.dto.ProductTypeDto;
import com.orbenox.erp.domain.product.entity.ProductType;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ProductTypeMapper {
    ProductType toEntity(ProductTypeCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(ProductTypeDto dto, @MappingTarget ProductType entity);
}

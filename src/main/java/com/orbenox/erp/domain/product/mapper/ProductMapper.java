package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductDto;
import com.orbenox.erp.domain.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductDto dto);

    @Mapping(target = "defaultUnit", ignore = true)
    void updateEntityFromDto(ProductDto dto, @MappingTarget Product entity);
}

package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductDto;
import com.orbenox.erp.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductDto dto);

    void updateEntityFromDto(ProductDto dto, @MappingTarget Product entity);
}

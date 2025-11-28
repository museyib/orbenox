package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductDto;
import com.orbenox.erp.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BrandMapper.class, ProductGroupMapper.class, ProductMapper.class})
public interface ProductMapper {
    ProductDto toDto(Product entity);
    Product toEntity(ProductDto dto);
    List<ProductDto> toDtoList(List<Product> entityList);
    void updateEntityFromDto(ProductDto dto, @MappingTarget Product entity);
}

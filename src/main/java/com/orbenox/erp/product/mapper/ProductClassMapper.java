package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductClassDto;
import com.orbenox.erp.product.entity.ProductClass;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductClassMapper {
    ProductClassDto toDto(ProductClass entity);
    ProductClass toEntity(ProductClassDto dto);
    List<ProductClassDto> toDtoList(List<ProductClass> entityList);
    void updateEntityFromDto(ProductClassDto dto, @MappingTarget ProductClass entity);
}

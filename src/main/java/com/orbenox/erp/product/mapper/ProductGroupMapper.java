package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductGroupDto;
import com.orbenox.erp.product.entity.ProductGroup;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductGroupMapper {
    ProductGroupDto toDto(ProductGroup entity);
    ProductGroup toEntity(ProductGroupDto dto);
    List<ProductGroupDto> toDtoList(List<ProductGroup> entityList);
    void updateEntityFromDto(ProductGroupDto dto, @MappingTarget ProductGroup entity);
}

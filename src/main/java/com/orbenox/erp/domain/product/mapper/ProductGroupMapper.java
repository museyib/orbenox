package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductGroupDto;
import com.orbenox.erp.domain.product.entity.ProductGroup;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = ProductGroupParentMapper.class)
public interface ProductGroupMapper {
    ProductGroup toEntity(ProductGroupDto dto);

    void updateEntityFromDto(ProductGroupDto dto, @MappingTarget ProductGroup entity);
}

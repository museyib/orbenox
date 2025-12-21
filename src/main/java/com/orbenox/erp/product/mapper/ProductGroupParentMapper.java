package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductGroupDto;
import com.orbenox.erp.product.entity.ProductGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductGroupParentMapper {
    ProductGroup toEntity(ProductGroupDto.Parent dto);
}

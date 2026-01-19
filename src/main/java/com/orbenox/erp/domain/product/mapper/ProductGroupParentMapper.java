package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductGroupUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductGroupParentMapper {
    ProductGroup toEntity(ProductGroupUpdateDto.Parent dto);
}

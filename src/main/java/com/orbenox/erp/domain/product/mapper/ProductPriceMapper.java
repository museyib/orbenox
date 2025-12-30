package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.entity.ProductPrice;
import com.orbenox.erp.domain.product.dto.ProductPriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductPriceMapper {
    ProductPrice toEntity(ProductPriceDto dto);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "priceList", ignore = true)
    @Mapping(target = "unit", ignore = true)
    void updateEntityFromDto(ProductPriceDto dto, @MappingTarget ProductPrice entity);
}

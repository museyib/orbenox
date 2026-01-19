package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductPriceCreateDto;
import com.orbenox.erp.domain.product.dto.ProductPriceUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductPrice;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ProductPriceMapper {
    ProductPrice toEntity(ProductPriceCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "priceList", ignore = true)
    @Mapping(target = "unit", ignore = true)
    void updateEntityFromDto(ProductPriceUpdateDto dto, @MappingTarget ProductPrice entity);
}

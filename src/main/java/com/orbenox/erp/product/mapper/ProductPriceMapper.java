package com.orbenox.erp.product.mapper;

import com.orbenox.erp.price.PriceListMapper;
import com.orbenox.erp.product.dto.ProductPriceDto;
import com.orbenox.erp.product.entity.ProductPrice;
import com.orbenox.erp.unit.UnitMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, PriceListMapper.class, UnitMapper.class})
public interface ProductPriceMapper {
    ProductPriceDto toDto(ProductPrice entity);
    ProductPrice toEntity(ProductPriceDto dto);
    List<ProductPriceDto> toDtoList(List<ProductPrice> entityList);
    List<ProductPrice> toEntityList(List<ProductPriceDto> dtoList);
    void updateEntityFromDto(ProductPriceDto dto, @MappingTarget ProductPrice entity);
}

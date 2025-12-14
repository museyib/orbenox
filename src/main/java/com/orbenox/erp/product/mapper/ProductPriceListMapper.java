package com.orbenox.erp.product.mapper;

import com.orbenox.erp.price.PriceListMapper;
import com.orbenox.erp.product.dto.ProductPriceListDto;
import com.orbenox.erp.product.entity.ProductPriceList;
import com.orbenox.erp.unit.UnitMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, PriceListMapper.class, UnitMapper.class})
public interface ProductPriceListMapper {
    ProductPriceListDto toDto(ProductPriceList entity);
    ProductPriceList toEntity(ProductPriceListDto dto);
    List<ProductPriceListDto> toDtoList(List<ProductPriceList> entityList);
    List<ProductPriceList> toEntityList(List<ProductPriceListDto> dtoList);
    void updateEntityFromDto(ProductPriceListDto dto, @MappingTarget ProductPriceList entity);
}

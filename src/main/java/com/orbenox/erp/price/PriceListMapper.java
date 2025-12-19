package com.orbenox.erp.price;

import com.orbenox.erp.currency.CurrencyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {PriceListParentMapper.class, CurrencyMapper.class})
public interface PriceListMapper {
    PriceList toEntity(PriceListDto dto);
    @Mapping(target = "currency", ignore = true)
    void updateEntityFromDto(PriceListDto dto, @MappingTarget PriceList entity);
}

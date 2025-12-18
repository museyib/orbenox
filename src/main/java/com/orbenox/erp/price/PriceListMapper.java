package com.orbenox.erp.price;

import com.orbenox.erp.currency.CurrencyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CurrencyMapper.class, PriceListParentMapper.class})
public interface PriceListMapper {
    PriceList toEntity(PriceListDto dto);
    void updateEntityFromDto(PriceListDto dto, @MappingTarget PriceList entity);
}

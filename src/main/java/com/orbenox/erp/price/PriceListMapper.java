package com.orbenox.erp.price;

import com.orbenox.erp.currency.CurrencyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CurrencyMapper.class})
public interface PriceListMapper {
    PriceListDto toDto(PriceList entity);
    PriceList toEntity(PriceListDto dto);
    List<PriceListDto> toDtoList(List<PriceList> entityList);
    void updateEntityFromDto(PriceListDto dto, @MappingTarget PriceList entity);
}

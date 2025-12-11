package com.orbenox.erp.product.mapper;

import com.orbenox.erp.currency.CurrencyMapper;
import com.orbenox.erp.product.dto.PriceListDto;
import com.orbenox.erp.product.entity.PriceList;
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

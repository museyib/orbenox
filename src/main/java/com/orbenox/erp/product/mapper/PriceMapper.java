package com.orbenox.erp.product.mapper;

import com.orbenox.erp.currency.CurrencyMapper;
import com.orbenox.erp.product.dto.PriceDto;
import com.orbenox.erp.product.entity.Price;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CurrencyMapper.class})
public interface PriceMapper {
    PriceDto toDto(Price entity);
    Price toEntity(PriceDto dto);
    List<PriceDto> toDtoList(List<Price> entityList);
    void updateEntityFromDto(PriceDto dto, @MappingTarget Price entity);
}

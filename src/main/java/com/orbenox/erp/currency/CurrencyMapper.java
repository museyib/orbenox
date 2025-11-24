package com.orbenox.erp.currency;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyDto toDto(Currency entity);
    Currency toEntity(CurrencyDto dto);
    List<CurrencyDto> toDtoList(List<Currency> entityList);
    void updateEntityFromDto(CurrencyDto dto, @MappingTarget Currency entity);
}

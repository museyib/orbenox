package com.orbenox.erp.currency;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    Currency toEntity(CurrencyDto dto);

    void updateEntityFromDto(CurrencyDto dto, @MappingTarget Currency entity);
}

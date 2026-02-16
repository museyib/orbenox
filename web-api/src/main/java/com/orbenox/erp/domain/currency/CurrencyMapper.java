package com.orbenox.erp.domain.currency;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    Currency toEntity(CurrencyCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(CurrencyUpdateDto dto, @MappingTarget Currency entity);
}

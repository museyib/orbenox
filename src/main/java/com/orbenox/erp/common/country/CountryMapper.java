package com.orbenox.erp.common.country;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    CountryDto toDto(Country entity);
    Country toEntity(CountryDto dto);
    List<CountryDto> toDtoList(List<Country> entityList);
    void updateEntityFromDto(CountryDto dto, @MappingTarget Country entity);
}

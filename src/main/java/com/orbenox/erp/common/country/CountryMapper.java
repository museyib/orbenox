package com.orbenox.erp.common.country;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    Country toEntity(CountryDto dto);
    void updateEntityFromDto(CountryDto dto, @MappingTarget Country entity);
}

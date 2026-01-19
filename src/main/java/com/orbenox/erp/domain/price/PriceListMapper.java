package com.orbenox.erp.domain.price;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = {PriceListParentMapper.class})
public interface PriceListMapper {
    PriceList toEntity(PriceListCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "currency", ignore = true)
    void updateEntityFromDto(PriceListUpdateDto dto, @MappingTarget PriceList entity);
}

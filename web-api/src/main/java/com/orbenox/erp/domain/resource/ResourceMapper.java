package com.orbenox.erp.domain.resource;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    Resource toEntity(ResourceCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "actions", ignore = true)
    void updateEntityFromDto(ResourceUpdateDto dto, @MappingTarget Resource entity);
}

package com.orbenox.erp.common.resource;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    Resource toEntity(ResourceDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(UpdateResourceRequest dto, @MappingTarget Resource entity);
}

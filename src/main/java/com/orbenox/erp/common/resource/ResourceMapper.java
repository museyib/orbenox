package com.orbenox.erp.common.resource;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    Resource toEntity(ResourceDto dto);
    void updateEntityFromDto(ResourceDto dto, @MappingTarget Resource entity);
}

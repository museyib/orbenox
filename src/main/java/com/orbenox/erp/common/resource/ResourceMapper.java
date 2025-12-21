package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.ActionMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = ActionMapper.class)
public interface ResourceMapper {
    ResourceDto toDto(Resource entity);
    Resource toEntity(ResourceDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(UpdateResourceRequest dto, @MappingTarget Resource entity);
}

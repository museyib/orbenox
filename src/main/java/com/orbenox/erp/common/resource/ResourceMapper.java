package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.ActionMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = ActionMapper.class)
public interface ResourceMapper {
    ResourceDto toDto(Resource resource);
    Resource toEntity(ResourceDto dto);
    List<ResourceDto> toDTOList(List<Resource> resources);
    void updateEntityFromDto(ResourceDto dto, @MappingTarget Resource resource);
}

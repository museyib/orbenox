package com.orbenox.erp.common.action;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Set;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ActionMapper {
    Action toEntity(ActionDto dto);

    Set<Action> toEntityList(Set<ActionDto> dtoSet);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(ActionDto dto, @MappingTarget Action action);
}

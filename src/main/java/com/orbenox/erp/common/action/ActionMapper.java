package com.orbenox.erp.common.action;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActionMapper {
    List<ActionDto> toDTOList(List<Action> actions);
}

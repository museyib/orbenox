package com.orbenox.erp.security.mapper;

import com.orbenox.erp.security.dto.UserDto;
import com.orbenox.erp.security.entity.AppUser;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface UserMapper {
    AppUser toEntity(UserDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "roles", ignore = true)
    void updateEntityFromDto(UserDto dto, @MappingTarget AppUser entity);
}

package com.orbenox.erp.security.mapper;

import com.orbenox.erp.security.dto.CreateUserRequest;
import com.orbenox.erp.security.dto.UpdateUserRequest;
import com.orbenox.erp.security.dto.UserDto;
import com.orbenox.erp.security.entity.AppUser;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserDto toDto(AppUser user);
    AppUser toEntity(CreateUserRequest user);
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(UpdateUserRequest dto, @MappingTarget AppUser user);
}

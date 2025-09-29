package com.orbenox.erp.security.dto.mapper;

import com.orbenox.erp.security.dto.CreateUserRequest;
import com.orbenox.erp.security.dto.UpdateUserRequest;
import com.orbenox.erp.security.dto.UserDto;
import com.orbenox.erp.security.entity.AppUser;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserDto toDTO(AppUser user);
    List<UserDto> toDTOList(List<AppUser> users);
    AppUser toEntity(CreateUserRequest user);
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDTO(UpdateUserRequest dto,  @MappingTarget AppUser user);
}

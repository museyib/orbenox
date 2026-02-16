package com.orbenox.erp.domain.businesspartner;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface BusinessPartnerRoleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "partner", ignore = true)
    BusinessPartnerRole toEntity(BusinessPartnerRoleCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "partner", ignore = true)
    void updateEntityFromDto(BusinessPartnerRoleUpdateDto dto, @MappingTarget BusinessPartnerRole entity);
}

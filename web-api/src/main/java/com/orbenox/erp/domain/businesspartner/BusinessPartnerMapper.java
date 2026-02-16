package com.orbenox.erp.domain.businesspartner;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface BusinessPartnerMapper {
    BusinessPartner toEntity(BusinessPartnerCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(BusinessPartnerUpdateDto dto, @MappingTarget BusinessPartner entity);
}

package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.BrandCreateDto;
import com.orbenox.erp.domain.product.dto.BrandUpdateDto;
import com.orbenox.erp.domain.product.entity.Brand;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toEntity(BrandCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(BrandUpdateDto dto, @MappingTarget Brand entity);
}

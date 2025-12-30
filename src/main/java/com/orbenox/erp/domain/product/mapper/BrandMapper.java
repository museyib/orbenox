package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.BrandDto;
import com.orbenox.erp.domain.product.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toEntity(BrandDto dto);

    void updateEntityFromDto(BrandDto dto, @MappingTarget Brand entity);
}

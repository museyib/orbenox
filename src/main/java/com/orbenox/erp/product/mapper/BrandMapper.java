package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.BrandDto;
import com.orbenox.erp.product.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toEntity(BrandDto dto);

    void updateEntityFromDto(BrandDto dto, @MappingTarget Brand entity);
}

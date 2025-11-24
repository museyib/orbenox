package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.BrandDto;
import com.orbenox.erp.product.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandDto toDto(Brand entity);
    Brand toEntity(BrandDto dto);
    List<BrandDto> toDtoList(List<Brand> entityList);
    void updateEntityFromDto(BrandDto dto, @MappingTarget Brand entity);
}

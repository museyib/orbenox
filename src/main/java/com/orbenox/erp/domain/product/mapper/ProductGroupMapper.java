package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductGroupCreateDto;
import com.orbenox.erp.domain.product.dto.ProductGroupDto;
import com.orbenox.erp.domain.product.entity.ProductGroup;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = ProductGroupParentMapper.class)
public interface ProductGroupMapper {
    ProductGroup toEntity(ProductGroupCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateEntityFromDto(ProductGroupDto dto, @MappingTarget ProductGroup entity);
}

package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductCreateDto;
import com.orbenox.erp.domain.product.dto.ProductUpdateDto;
import com.orbenox.erp.domain.product.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "productType", ignore = true)
    @Mapping(target = "productClass", ignore = true)
    @Mapping(target = "productGroup", ignore = true)
    @Mapping(target = "productCategory", ignore = true)
    @Mapping(target = "producer", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "defaultUnit", ignore = true)
    void updateEntityFromDto(ProductUpdateDto dto, @MappingTarget Product entity);
}

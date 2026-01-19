package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductBarcodeCreateDto;
import com.orbenox.erp.domain.product.dto.ProductBarcodeUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductBarcode;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ProductBarcodeMapper {
    ProductBarcode toEntity(ProductBarcodeCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "unit", ignore = true)
    void updateEntityFromDto(ProductBarcodeUpdateDto dto, @MappingTarget ProductBarcode entity);
}

package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.product.dto.ProductBarcodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductBarcodeMapper {
    ProductBarcode toEntity(ProductBarcodeDto dto);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "unit", ignore = true)
    void updateEntityFromDto(ProductBarcodeDto dto, @MappingTarget ProductBarcode entity);
}

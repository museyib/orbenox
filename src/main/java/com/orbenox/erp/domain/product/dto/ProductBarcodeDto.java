package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.unit.UnitDto;

public record ProductBarcodeDto(Long id, ProductDto product, UnitDto unit, String barcode) {
}

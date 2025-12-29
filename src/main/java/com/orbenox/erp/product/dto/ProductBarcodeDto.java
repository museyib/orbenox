package com.orbenox.erp.product.dto;

import com.orbenox.erp.unit.UnitDto;

public record ProductBarcodeDto(Long id, ProductDto product, UnitDto unit, String barcode) {
}

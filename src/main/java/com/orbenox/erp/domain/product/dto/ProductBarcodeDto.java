package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.unit.UnitDto;

/**
 * DTO for {@link ProductBarcode}
 */

public record ProductBarcodeDto(Long id, ProductDto product, UnitDto unit, String barcode) {
}

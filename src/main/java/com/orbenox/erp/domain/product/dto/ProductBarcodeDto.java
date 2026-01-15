package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.unit.UnitDto;
import jakarta.validation.constraints.NotNull;


/**
 * DTO for {@link ProductBarcode}
 */

public record ProductBarcodeDto(Long id,
                                @NotNull(message = "{product.notNull}") ProductDto product,
                                @NotNull(message = "{unit.notNull}") UnitDto unit,
                                String barcode) {
}

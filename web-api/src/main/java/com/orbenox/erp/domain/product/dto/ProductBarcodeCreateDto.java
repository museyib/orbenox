package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.validation.BarcodeValidator;
import jakarta.validation.constraints.NotNull;


/**
 * DTO for {@link ProductBarcode}
 */

public record ProductBarcodeCreateDto(@NotNull(message = "{product.notNull}") Long productId,
                                      @NotNull(message = "{unit.notNull}") Long unitId,
                                      @BarcodeValidator String barcode) {
}

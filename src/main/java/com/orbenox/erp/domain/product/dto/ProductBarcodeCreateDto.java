package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.unit.UnitUpdateDto;
import jakarta.validation.constraints.NotNull;


/**
 * DTO for {@link ProductBarcode}
 */

public record ProductBarcodeCreateDto(@NotNull(message = "{product.notNull}") ProductUpdateDto product,
                                      @NotNull(message = "{unit.notNull}") UnitUpdateDto unit,
                                      String barcode) {
}

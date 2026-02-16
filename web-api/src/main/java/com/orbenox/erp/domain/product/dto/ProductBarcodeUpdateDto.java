package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.validation.BarcodeValidator;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;


/**
 * DTO for {@link ProductBarcode}
 */

public record ProductBarcodeUpdateDto(Long id,
                                      @NotNull(message = "{product.notNull}") Long productId,
                                      @NotNull(message = "{unit.notNull}") Long unitId,
                                      @BarcodeValidator String barcode) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductBarcodeUpdateDto that = (ProductBarcodeUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

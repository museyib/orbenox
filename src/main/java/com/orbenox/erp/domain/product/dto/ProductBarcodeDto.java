package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.unit.UnitDto;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;


/**
 * DTO for {@link ProductBarcode}
 */

public record ProductBarcodeDto(Long id,
                                @NotNull(message = "{product.notNull}") ProductDto product,
                                @NotNull(message = "{unit.notNull}") UnitDto unit,
                                String barcode) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductBarcodeDto that = (ProductBarcodeDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

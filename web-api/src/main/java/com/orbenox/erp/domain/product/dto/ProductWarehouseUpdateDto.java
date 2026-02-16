package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO for {@link ProductWarehouse}
 */
public record ProductWarehouseUpdateDto(Long id,
                                        @NotNull(message = "{product.notNull}") Long productId,
                                        @NotNull(message = "{warehouse.notNull}") Long warehouseId,
                                        BigDecimal quantity,
                                        BigDecimal reservedQuantity,
                                        BigDecimal minQuantity,
                                        BigDecimal maxQuantity) implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductWarehouseUpdateDto that = (ProductWarehouseUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

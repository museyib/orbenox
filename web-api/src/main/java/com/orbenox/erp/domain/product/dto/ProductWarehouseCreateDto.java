package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link ProductWarehouse}
 */
public record ProductWarehouseCreateDto(@NotNull(message = "{product.notNull}") Long productId,
                                        @NotNull(message = "{warehouse.notNull}") Long warehouseId,
                                        BigDecimal minQuantity,
                                        BigDecimal maxQuantity) implements Serializable {
}

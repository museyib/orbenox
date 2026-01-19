package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import com.orbenox.erp.domain.warehouse.WarehouseUpdateDto;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link ProductWarehouse}
 */
public record ProductWarehouseCreateDto(@NotNull(message = "{product.notNull}") ProductUpdateDto product,
                                        @NotNull(message = "{warehouse.notNull}") WarehouseUpdateDto warehouse,
                                        BigDecimal quantity,
                                        BigDecimal reservedQuantity,
                                        BigDecimal minQuantity,
                                        BigDecimal maxQuantity) implements Serializable {
}
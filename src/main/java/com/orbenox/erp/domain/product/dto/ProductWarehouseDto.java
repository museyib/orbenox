package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import com.orbenox.erp.domain.warehouse.WarehouseDto;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link ProductWarehouse}
 */
public record ProductWarehouseDto(Long id,
                                  @NotNull(message = "{product.notNull}") ProductDto product,
                                  @NotNull(message = "{warehouse.notNull}") WarehouseDto warehouse,
                                  BigDecimal quantity,
                                  BigDecimal reservedQuantity,
                                  BigDecimal freeQuantity,
                                  BigDecimal minQuantity,
                                  BigDecimal maxQuantity) implements Serializable {
}
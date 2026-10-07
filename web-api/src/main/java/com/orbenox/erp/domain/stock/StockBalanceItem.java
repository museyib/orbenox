package com.orbenox.erp.domain.stock;

import java.math.BigDecimal;

public record StockBalanceItem(
        Product product,
        Warehouse warehouse,
        BigDecimal quantity,
        BigDecimal reservedQuantity,
        BigDecimal freeQuantity) {

    public Product getProduct() {
        return product;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getReservedQuantity() {
        return reservedQuantity;
    }

    public BigDecimal getFreeQuantity() {
        return freeQuantity;
    }

    public record Product(
            Long id,
            Unit defaultUnit,
            String code,
            String name,
            String description,
            String defaultBarcode) {
    }

    public record Warehouse(Long id, String code, String name, boolean enabled) {
    }

    public record Unit(Long id, String code, String name, boolean enabled) {
    }
}

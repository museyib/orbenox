package com.orbenox.erp.domain.warehouse;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * DTO for {@link Warehouse}
 */
public record WarehouseDto(Long id,
                           Boolean enabled,
                           @NotBlank(message = "{code.notBlank}") String code,
                           @NotBlank(message = "{name.notBlank}") String name) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseDto that = (WarehouseDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
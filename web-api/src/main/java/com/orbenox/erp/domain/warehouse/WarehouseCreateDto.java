package com.orbenox.erp.domain.warehouse;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link Warehouse}
 */
public record WarehouseCreateDto(boolean enabled,
                                 @NotBlank(message = "{code.notBlank}") String code,
                                 @NotBlank(message = "{name.notBlank}") String name) {
}
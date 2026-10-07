package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductType;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link ProductType}
 */
public record ProductTypeCreateDto(boolean enabled,
                                   @NotBlank(message = "{code.notBlank}") String code,
                                   @NotBlank(message = "{name.notBlank}") String name,
                                   String description) {
}
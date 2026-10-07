package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductCategory;
import jakarta.validation.constraints.NotBlank;


/**
 * DTO for {@link ProductCategory}
 */
public record ProductCategoryCreateDto(boolean enabled,
                                       @NotBlank(message = "{code.notBlank}") String code,
                                       @NotBlank(message = "{name.notBlank}") String name,
                                       String description) {
}
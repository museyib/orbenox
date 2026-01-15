package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.Brand;
import jakarta.validation.constraints.NotBlank;



/**
 * DTO for {@link Brand}
 */
public record BrandDto(Long id,
                       Boolean enabled,
                       @NotBlank(message = "{code.notBlank}") String code,
                       @NotBlank(message = "{name.notBlank}") String name,
                       String description) {
}
package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.Brand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


/**
 * DTO for {@link Brand}
 */
public record BrandCreateDto(boolean enabled,
                             @NotBlank(message = "{code.notBlank}")
                             @Size(min = 1, max = 50, message = "{code.size}")
                             @Pattern(regexp = "^[A-Z0-9-_]+$", message = "{code.pattern}")
                             String code,
                             @NotBlank(message = "{name.notBlank}")
                             @Size(min = 1, max = 255, message = "{name.size}")
                             String name,
                             @Size(max = 1000, message = "{description.size}")
                             String description) {
}
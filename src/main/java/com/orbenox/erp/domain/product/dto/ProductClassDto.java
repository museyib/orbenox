package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductClass;
import jakarta.validation.constraints.NotBlank;



/**
 * DTO for {@link ProductClass}
 */
public record ProductClassDto(Long id,
                              Boolean enabled,
                              @NotBlank(message = "{code.notBlank}") String code,
                              @NotBlank(message = "{name.notBlank}") String name,
                              String description) {
}
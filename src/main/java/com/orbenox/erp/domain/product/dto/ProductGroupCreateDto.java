package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductGroup;
import jakarta.validation.constraints.NotBlank;


/**
 * DTO for {@link ProductGroup}
 */
public record ProductGroupCreateDto(Boolean enabled,
                                    @NotBlank(message = "{code.notBlank}") String code,
                                    @NotBlank(message = "{name.notBlank}") String name,
                                    String description,
                                    String slug,
                                    Parent parent) {
    public record Parent(Long id, Boolean enabled, String code, String name) {
    }
}
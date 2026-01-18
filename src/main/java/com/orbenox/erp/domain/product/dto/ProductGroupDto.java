package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductGroup;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;


/**
 * DTO for {@link ProductGroup}
 */
public record ProductGroupDto(Long id,
                              Boolean enabled,
                              @NotBlank(message = "{code.notBlank}") String code,
                              @NotBlank(message = "{name.notBlank}") String name,
                              String description,
                              String slug,
                              Parent parent) {
    public record Parent(Long id, Boolean enabled, String code, String name) {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductGroupDto that = (ProductGroupDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductCategory;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;


/**
 * DTO for {@link ProductCategory}
 */
public record ProductCategoryUpdateDto(Long id,
                                       boolean enabled,
                                       @NotBlank(message = "{code.notBlank}") String code,
                                       @NotBlank(message = "{name.notBlank}") String name,
                                       String description) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductCategoryUpdateDto that = (ProductCategoryUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
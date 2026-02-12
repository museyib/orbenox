package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.Brand;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;


/**
 * DTO for {@link Brand}
 */
public record BrandUpdateDto(Long id,
                             boolean enabled,
                             @NotBlank(message = "{code.notBlank}") String code,
                             @NotBlank(message = "{name.notBlank}") String name,
                             String description) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BrandUpdateDto brandDto = (BrandUpdateDto) o;
        return Objects.equals(id, brandDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
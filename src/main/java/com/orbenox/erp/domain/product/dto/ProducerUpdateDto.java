package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.Producer;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;


/**
 * DTO for {@link Producer}
 */
public record ProducerUpdateDto(Long id,
                                boolean enabled,
                                @NotBlank(message = "{code.notBlank}") String code,
                                @NotBlank(message = "{name.notBlank}") String name,
                                String description) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProducerUpdateDto that = (ProducerUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
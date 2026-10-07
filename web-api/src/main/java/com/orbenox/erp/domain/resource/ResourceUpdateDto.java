package com.orbenox.erp.domain.resource;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link Resource}
 */
public record ResourceUpdateDto(Long id,
                                boolean enabled,
                                @NotBlank(message = "{code.notBlank}") String code,
                                @NotBlank(message = "{name.notBlank}") String name,
                                Set<String> actions) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ResourceUpdateDto that = (ResourceUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
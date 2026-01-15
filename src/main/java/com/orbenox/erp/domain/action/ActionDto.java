package com.orbenox.erp.domain.action;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * DTO for {@link Action}
 */
public record ActionDto(Long id,
                        Boolean enabled,
                        @NotBlank(message = "{code.notBlank}") String code,
                        @NotBlank(message = "{name.notBlank}") String name) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ActionDto actionDto = (ActionDto) o;
        return Objects.equals(id, actionDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
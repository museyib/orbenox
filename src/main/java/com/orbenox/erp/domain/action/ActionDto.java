package com.orbenox.erp.domain.action;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link for Action}
 */
public record ActionDto(Long id, String code, String name, Boolean enabled) implements Serializable {
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
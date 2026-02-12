package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.AppRole;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * DTO for {@link AppRole}
 */
public record RoleUpdateDto(Long id,
                            boolean enabled,
                            @NotBlank(message = "{code.notBlank}") String code,
                            @NotBlank(message = "{name.notBlank}") String name) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoleUpdateDto roleDto = (RoleUpdateDto) o;
        return Objects.equals(id, roleDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package com.orbenox.erp.domain.resource;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/**
 * DTO for {@link Resource}
 */
public record ResourceCreateDto(boolean enabled,
                                @NotBlank(message = "{code.notBlank}") String code,
                                @NotBlank(message = "{name.notBlank}") String name,
                                Set<String> actions) {
}
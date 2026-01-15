package com.orbenox.erp.domain.resource;

import com.orbenox.erp.domain.action.ActionDto;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/**
 * DTO for {@link Resource}
 */
public record ResourceDto(Long id,
                          Boolean enabled,
                          @NotBlank(message = "{code.notBlank}") String code,
                          @NotBlank(message = "{name.notBlank}") String name,
                          Set<ActionDto> actions) {
}
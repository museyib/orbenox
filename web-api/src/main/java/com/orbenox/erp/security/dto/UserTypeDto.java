package com.orbenox.erp.security.dto;

import com.orbenox.erp.security.entity.UserType;
import jakarta.validation.constraints.NotBlank;


/**
 * DTO for {@link UserType}
 */
public record UserTypeDto(Long id,
                          boolean enabled,
                          @NotBlank(message = "{code.notBlank}") String code,
                          @NotBlank(message = "{name.notBlank}") String name) {
}
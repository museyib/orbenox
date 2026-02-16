package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.Producer;
import jakarta.validation.constraints.NotBlank;


/**
 * DTO for {@link Producer}
 */
public record ProducerCreateDto(boolean enabled,
                                @NotBlank(message = "{code.notBlank}") String code,
                                @NotBlank(message = "{name.notBlank}") String name,
                                String description) {
}
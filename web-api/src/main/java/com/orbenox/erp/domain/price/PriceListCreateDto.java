package com.orbenox.erp.domain.price;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for {@link PriceList}
 */
public record PriceListCreateDto(boolean enabled,
                                 @NotBlank(message = "{code.notBlank}") String code,
                                 @NotBlank(message = "{name.notBlank}") String name,
                                 @NotNull(message = "{currency.notNull}") Long currencyId,
                                 BigDecimal factorToParent,
                                 Long parentId,
                                 Short roundLength) {
}
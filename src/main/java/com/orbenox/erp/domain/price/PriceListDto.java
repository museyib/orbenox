package com.orbenox.erp.domain.price;

import com.orbenox.erp.domain.currency.CurrencyDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for {@link PriceList}
 */
public record PriceListDto(Long id,
                           Boolean enabled,
                           @NotBlank(message = "{code.notBlank}") String code,
                           @NotBlank(message = "{name.notBlank}") String name,
                           @NotNull(message = "{currency.notNull}") CurrencyDto currency,
                           BigDecimal factorToParent,
                           Parent parent,
                           Short roundLength) {
    public record Parent(Long id, Boolean enabled, String code, String name) {
    }
}
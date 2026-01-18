package com.orbenox.erp.domain.price;

import com.orbenox.erp.domain.currency.CurrencyDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PriceListDto that = (PriceListDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
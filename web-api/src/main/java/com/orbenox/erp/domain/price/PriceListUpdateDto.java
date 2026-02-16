package com.orbenox.erp.domain.price;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO for {@link PriceList}
 */
public record PriceListUpdateDto(Long id,
                                 boolean enabled,
                                 @NotBlank(message = "{code.notBlank}") String code,
                                 @NotBlank(message = "{name.notBlank}") String name,
                                 @NotNull(message = "{currency.notNull}") Long currencyId,
                                 BigDecimal factorToParent,
                                 Long parentId,
                                 Short roundLength) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PriceListUpdateDto that = (PriceListUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
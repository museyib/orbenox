package com.orbenox.erp.price;

import com.orbenox.erp.currency.CurrencyDto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link PriceList}
 */
public record PriceListDto(Long id, Boolean enabled, String code, String name, CurrencyDto currency, BigDecimal factorToParent,
                           PriceListDto parent) implements Serializable {
}
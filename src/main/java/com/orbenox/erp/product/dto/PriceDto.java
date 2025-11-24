package com.orbenox.erp.product.dto;

import com.orbenox.erp.currency.CurrencyDto;
import com.orbenox.erp.product.entity.Price;

import java.io.Serializable;

/**
 * DTO for {@link Price}
 */
public record PriceDto(Long id, Boolean enabled, String code, String name, CurrencyDto currency,
                       PriceDto parent) implements Serializable {
}
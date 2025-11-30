package com.orbenox.erp.product.dto;

import com.orbenox.erp.currency.CurrencyDto;
import com.orbenox.erp.product.entity.Price;
import com.orbenox.erp.product.entity.ProductPrice;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link Price}
 */
public record PriceDto(Long id, Boolean enabled, String code, String name, CurrencyDto currency,
                       PriceDto parent) implements Serializable {
}
package com.orbenox.erp.product.dto;

import com.orbenox.erp.currency.CurrencyDto;
import com.orbenox.erp.product.entity.PriceList;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link PriceList}
 */
public record PriceListDto(Long id, Boolean enabled, String code, String name, CurrencyDto currency, BigDecimal factorToParent,
                           PriceListDto parent) implements Serializable {
}
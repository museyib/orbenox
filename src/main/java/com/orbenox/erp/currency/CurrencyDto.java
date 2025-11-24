package com.orbenox.erp.currency;

import java.io.Serializable;

/**
 * DTO for {@link Currency}
 */
public record CurrencyDto(Long id, Boolean enabled, String code, String name) implements Serializable {
}
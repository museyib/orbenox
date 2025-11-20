package com.orbenox.erp.product.dbo;

import com.orbenox.erp.product.entity.Producer;

import java.io.Serializable;

/**
 * DTO for {@link Producer}
 */
public record ProducerDto(Long id, String code, String name, Boolean active) implements Serializable {
}
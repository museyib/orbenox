package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.Producer;

import java.io.Serializable;

/**
 * DTO for {@link Producer}
 */
public record ProducerDto(Long id, String code, String name, Boolean enabled) implements Serializable {
}
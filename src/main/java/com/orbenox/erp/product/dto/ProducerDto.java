package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.Producer;

import java.io.Serializable;

/**
 * DTO for {@link Producer}
 */
public record ProducerDto(Long id, Boolean enabled, String code, String name, String description)
        implements Serializable {
}
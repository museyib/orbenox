package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.Producer;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link Producer}
 */
public record ProducerDto(Long id, Boolean enable, String code, String named, String description,
                          Set<ProductDto> products) implements Serializable {
}
package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductType;

import java.io.Serializable;

/**
 * DTO for {@link ProductType}
 */
public record ProductTypeDto(Long id, Boolean enabled, String code, String name, String description)
        implements Serializable {
}
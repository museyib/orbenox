package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.ProductCategory;

import java.io.Serializable;

/**
 * DTO for {@link ProductCategory}
 */
public record ProductCategoryDto(Long id, Boolean enabled, String code, String name, String description)
        implements Serializable {
}
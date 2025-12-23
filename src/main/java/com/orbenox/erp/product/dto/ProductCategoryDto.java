package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.ProductCategory;

import java.io.Serializable;

/**
 * DTO for {@link ProductCategory}
 */
public record ProductCategoryDto(Long id, Boolean enabled, String code, String name, String description)
        implements Serializable {
}
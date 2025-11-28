package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.ProductCategory;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link ProductCategory}
 */
public record ProductCategoryDto(Long id, Boolean enabled, String code, String name, String description,
                                 Set<ProductDto> products) implements Serializable {
}
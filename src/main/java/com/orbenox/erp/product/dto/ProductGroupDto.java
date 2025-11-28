package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.ProductGroup;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link ProductGroup}
 */
public record ProductGroupDto(Long id, Boolean enabled, String code, String name, String description, String slug,
                              ProductGroupDto parent, Set<ProductDto> products) implements Serializable {
}
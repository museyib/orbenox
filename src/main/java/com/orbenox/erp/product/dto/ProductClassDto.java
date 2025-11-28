package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.ProductClass;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link ProductClass}
 */
public record ProductClassDto(Long id, Boolean enabled, String code, String name, String description,
                              Set<ProductDto> products) implements Serializable {
}
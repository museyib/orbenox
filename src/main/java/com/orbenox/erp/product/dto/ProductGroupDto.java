package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.ProductGroup;

import java.io.Serializable;

/**
 * DTO for {@link ProductGroup}
 */
public record ProductGroupDto(Long id, Boolean enabled, String code, String name, String description, String slug,
                              Parent parent)
        implements Serializable {
    public record Parent(Long id, Boolean enabled, String code, String name) {
    }
}
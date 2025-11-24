package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.Category;

import java.io.Serializable;

/**
 * DTO for {@link Category}
 */
public record CategoryDto(Long id, String code, String name, String description, String slug, CategoryDto parent,
                          Boolean enabled) implements Serializable {
}
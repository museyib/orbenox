package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.Brand;

import java.io.Serializable;

/**
 * DTO for {@link Brand}
 */
public record BrandDto(Long id, String code, String name, String description, Boolean enabled) implements Serializable {
}
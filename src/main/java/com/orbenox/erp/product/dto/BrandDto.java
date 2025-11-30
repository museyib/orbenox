package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.entity.Brand;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link Brand}
 */
public record BrandDto(Long id, Boolean enabled, String code, String name, String description) implements Serializable {
}
package com.orbenox.erp.product.dto;

import com.orbenox.erp.common.country.CountryDto;
import com.orbenox.erp.product.entity.Product;
import com.orbenox.erp.unit.UnitDto;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link Product}
 */
public record ProductDto(Long id,
                         Boolean enabled,
                         String code,
                         String name,
                         String description,
                         @NotNull(message = "Brand cannot be null")
                         BrandDto brand,
                         @NotNull(message = "Product type cannot be null")
                         ProductTypeDto productType,
                         @NotNull(message = "Product class cannot be null")
                         ProductClassDto productClass,
                         @NotNull(message = "Product group cannot be null")
                         ProductGroupDto productGroup,
                         @NotNull(message = "Product category cannot be null")
                         ProductCategoryDto productCategory,
                         @NotNull(message = "Producer cannot be null")
                         ProducerDto producer,
                         @NotNull(message = "Country cannot be null")
                         CountryDto country,
                         @NotNull(message = "Unit cannot be null")
                         UnitDto defaultUnit,
                         @NotNull(message = "Barcode cannot be null")
                         String defaultBarcode) implements Serializable {
}
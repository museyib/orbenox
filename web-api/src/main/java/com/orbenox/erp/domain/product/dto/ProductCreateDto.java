package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.validation.BarcodeValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


/**
 * DTO for {@link Product}
 */
public record ProductCreateDto(boolean enabled,
                               @NotBlank(message = "{code.notBlank}")
                               @Size(min = 1, max = 50, message = "{code.size}")
                               @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "{code.pattern}")
                               String code,
                               @NotBlank(message = "{name.notBlank}")
                               @Size(min = 1, max = 255, message = "{name.size}")
                               String name,
                               @Size(max = 1000, message = "{description.size}")
                               String description,
                               @NotNull(message = "{brand.notNull}")
                               Long brandId,
                               @NotNull(message = "{productType.notNull}")
                               Long productTypeId,
                               @NotNull(message = "{productClass.notNull}")
                               Long productClassId,
                               @NotNull(message = "{productGroup.notNull}")
                               Long productGroupId,
                               @NotNull(message = "{productCategory.notNull}")
                               Long productCategoryId,
                               @NotNull(message = "{producer.notNull}")
                               Long producerId,
                               @NotNull(message = "{country.notNull}")
                               Long countryId,
                               @NotNull(message = "{unit.notNull}")
                               Long defaultUnitId,
                               @BarcodeValidator String defaultBarcode) {
}

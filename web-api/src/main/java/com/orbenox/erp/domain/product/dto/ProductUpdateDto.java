package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.validation.BarcodeValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;


/**
 * DTO for {@link Product}
 */
public record ProductUpdateDto(Long id,
                               boolean enabled,
                               @NotBlank(message = "{code.notBlank}") String code,
                               @NotBlank(message = "{name.notBlank}") String name,
                               String description,
                               @NotNull(message = "{brand.notNull}") Long brandId,
                               @NotNull(message = "{productType.notNull}") Long productTypeId,
                               @NotNull(message = "{productClass.notNull}") Long productClassId,
                               @NotNull(message = "{productGroup.notNull}") Long productGroupId,
                               @NotNull(message = "{productCategory.notNull}") Long productCategoryId,
                               @NotNull(message = "{producer.notNull}") Long producerId,
                               @NotNull(message = "{country.notNull}") Long countryId,
                               @NotNull(message = "{unit.notNull}") Long defaultUnitId,
                               @BarcodeValidator String defaultBarcode) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductUpdateDto that = (ProductUpdateDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.*;
import com.orbenox.erp.domain.product.entity.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCatalogMappersTest {
    private final BrandMapper brands = new BrandMapperImpl();
    private final ProducerMapper producers = new ProducerMapperImpl();
    private final ProductCategoryMapper categories = new ProductCategoryMapperImpl();
    private final ProductClassMapper classes = new ProductClassMapperImpl();
    private final ProductTypeMapper types = new ProductTypeMapperImpl();

    @Test
    void createMappersCopyEnabledCodeNameAndDescription() {
        Brand brand = brands.toEntity(new BrandCreateDto(true, "ACME", "Acme", "Brand description"));
        Producer producer = producers.toEntity(new ProducerCreateDto(false, "PROD", "Producer", "Producer description"));
        ProductCategory category = categories.toEntity(new ProductCategoryCreateDto(true, "CAT", "Category", "Category description"));
        ProductClass productClass = classes.toEntity(new ProductClassCreateDto(false, "CLS", "Class", "Class description"));
        ProductType type = types.toEntity(new ProductTypeCreateDto(true, "TYPE", "Type", "Type description"));

        assertCard(brand.isEnabled(), brand.getCode(), brand.getName(), brand.getDescription(),
                true, "ACME", "Acme", "Brand description");
        assertCard(producer.isEnabled(), producer.getCode(), producer.getName(), producer.getDescription(),
                false, "PROD", "Producer", "Producer description");
        assertCard(category.isEnabled(), category.getCode(), category.getName(), category.getDescription(),
                true, "CAT", "Category", "Category description");
        assertCard(productClass.isEnabled(), productClass.getCode(), productClass.getName(), productClass.getDescription(),
                false, "CLS", "Class", "Class description");
        assertCard(type.isEnabled(), type.getCode(), type.getName(), type.getDescription(),
                true, "TYPE", "Type", "Type description");
    }

    @Test
    void updateMappersCopySuppliedValuesToExistingEntities() {
        Brand brand = new Brand();
        Producer producer = new Producer();
        ProductCategory category = new ProductCategory();
        ProductClass productClass = new ProductClass();
        ProductType type = new ProductType();

        brands.updateEntityFromDto(new BrandUpdateDto(1L, true, "B2", "Brand 2", "updated brand"), brand);
        producers.updateEntityFromDto(new ProducerUpdateDto(2L, true, "P2", "Producer 2", "updated producer"), producer);
        categories.updateEntityFromDto(new ProductCategoryUpdateDto(3L, true, "C2", "Category 2", "updated category"), category);
        classes.updateEntityFromDto(new ProductClassUpdateDto(4L, true, "CL2", "Class 2", "updated class"), productClass);
        types.updateEntityFromDto(new ProductTypeUpdateDto(5L, true, "T2", "Type 2", "updated type"), type);

        assertCard(brand.isEnabled(), brand.getCode(), brand.getName(), brand.getDescription(),
                true, "B2", "Brand 2", "updated brand");
        assertCard(producer.isEnabled(), producer.getCode(), producer.getName(), producer.getDescription(),
                true, "P2", "Producer 2", "updated producer");
        assertCard(category.isEnabled(), category.getCode(), category.getName(), category.getDescription(),
                true, "C2", "Category 2", "updated category");
        assertCard(productClass.isEnabled(), productClass.getCode(), productClass.getName(), productClass.getDescription(),
                true, "CL2", "Class 2", "updated class");
        assertCard(type.isEnabled(), type.getCode(), type.getName(), type.getDescription(),
                true, "T2", "Type 2", "updated type");
    }

    @Test
    void updateMappersIgnoreNullOptionalTextFields() {
        Brand brand = new Brand();
        brand.setName("existing brand");
        brand.setDescription("existing description");
        Producer producer = new Producer();
        producer.setName("existing producer");
        producer.setDescription("existing description");
        ProductCategory category = new ProductCategory();
        category.setDescription("existing description");
        ProductClass productClass = new ProductClass();
        productClass.setDescription("existing description");
        ProductType type = new ProductType();
        type.setDescription("existing description");

        brands.updateEntityFromDto(new BrandUpdateDto(null, false, null, null, null), brand);
        producers.updateEntityFromDto(new ProducerUpdateDto(null, false, null, null, null), producer);
        categories.updateEntityFromDto(new ProductCategoryUpdateDto(null, false, null, null, null), category);
        classes.updateEntityFromDto(new ProductClassUpdateDto(null, false, null, null, null), productClass);
        types.updateEntityFromDto(new ProductTypeUpdateDto(null, false, null, null, null), type);

        assertThat(brand.getName()).isEqualTo("existing brand");
        assertThat(brand.getDescription()).isEqualTo("existing description");
        assertThat(producer.getName()).isEqualTo("existing producer");
        assertThat(producer.getDescription()).isEqualTo("existing description");
        assertThat(category.getDescription()).isEqualTo("existing description");
        assertThat(productClass.getDescription()).isEqualTo("existing description");
        assertThat(type.getDescription()).isEqualTo("existing description");
    }

    private void assertCard(boolean enabled, String code, String name, String description,
                            boolean expectedEnabled, String expectedCode, String expectedName, String expectedDescription) {
        assertThat(enabled).isEqualTo(expectedEnabled);
        assertThat(code).isEqualTo(expectedCode);
        assertThat(name).isEqualTo(expectedName);
        assertThat(description).isEqualTo(expectedDescription);
    }
}

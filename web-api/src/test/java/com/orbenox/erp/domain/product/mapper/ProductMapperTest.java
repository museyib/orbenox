package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.country.Country;
import com.orbenox.erp.domain.product.dto.ProductCreateDto;
import com.orbenox.erp.domain.product.dto.ProductUpdateDto;
import com.orbenox.erp.domain.product.entity.*;
import com.orbenox.erp.domain.unit.Unit;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {
    @Mock
    EntityManager entityManager;
    @InjectMocks
    ProductMapperImpl mapper;

    @Test
    void createMapsProductFieldsAndAllReferenceIds() {
        Brand brand = new Brand();
        ProductType type = new ProductType();
        ProductClass productClass = new ProductClass();
        ProductGroup group = new ProductGroup();
        ProductCategory category = new ProductCategory();
        Producer producer = new Producer();
        Country country = new Country();
        Unit unit = new Unit();
        when(entityManager.getReference(Brand.class, 1L)).thenReturn(brand);
        when(entityManager.getReference(ProductType.class, 2L)).thenReturn(type);
        when(entityManager.getReference(ProductClass.class, 3L)).thenReturn(productClass);
        when(entityManager.getReference(ProductGroup.class, 4L)).thenReturn(group);
        when(entityManager.getReference(ProductCategory.class, 5L)).thenReturn(category);
        when(entityManager.getReference(Producer.class, 6L)).thenReturn(producer);
        when(entityManager.getReference(Country.class, 7L)).thenReturn(country);
        when(entityManager.getReference(Unit.class, 8L)).thenReturn(unit);
        Product product = mapper.toEntity(new ProductCreateDto(true, "SKU-8", "Widget", "Details",
                1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, "barcode"));

        assertThat(product.isEnabled()).isTrue();
        assertThat(product.getCode()).isEqualTo("SKU-8");
        assertThat(product.getName()).isEqualTo("Widget");
        assertThat(product.getDescription()).isEqualTo("Details");
        assertThat(product.getDefaultBarcode()).isEqualTo("barcode");
        assertThat(product.getBrand()).isSameAs(brand);
        assertThat(product.getProductType()).isSameAs(type);
        assertThat(product.getProductClass()).isSameAs(productClass);
        assertThat(product.getProductGroup()).isSameAs(group);
        assertThat(product.getProductCategory()).isSameAs(category);
        assertThat(product.getProducer()).isSameAs(producer);
        assertThat(product.getCountry()).isSameAs(country);
        assertThat(product.getDefaultUnit()).isSameAs(unit);
        verify(entityManager).getReference(Brand.class, 1L);
        verify(entityManager).getReference(Unit.class, 8L);
    }

    @Test
    void updateMapsProvidedValuesAndKeepsNullableFieldsWhenOmitted() {
        Product product = new Product();
        product.setCode("prior-code");
        product.setName("prior-name");
        product.setDescription("prior description");
        product.setDefaultBarcode("prior-barcode");
        Brand brand = new Brand();
        Unit unit = new Unit();
        when(entityManager.getReference(Brand.class, 10L)).thenReturn(brand);
        when(entityManager.getReference(Unit.class, 11L)).thenReturn(unit);

        mapper.updateEntityFromDto(new ProductUpdateDto(12L, true, null, "new-name", null,
                10L, null, null, null, null, null, null, 11L, null), product);

        assertThat(product.getId()).isEqualTo(12L);
        assertThat(product.isEnabled()).isTrue();
        assertThat(product.getBrand()).isSameAs(brand);
        assertThat(product.getDefaultUnit()).isSameAs(unit);
        assertThat(product.getName()).isEqualTo("new-name");
        assertThat(product.getCode()).isEqualTo("prior-code");
        assertThat(product.getDescription()).isEqualTo("prior description");
        assertThat(product.getDefaultBarcode()).isEqualTo("prior-barcode");
        verify(entityManager, never()).getReference(eq(ProductType.class), anyLong());
        verify(entityManager, never()).getReference(eq(Country.class), anyLong());
    }

    @Test
    void zeroForeignKeyMapsToNull() {
        Product product = mapper.toEntity(new ProductCreateDto(false, "SKU-0", "No references", null,
                0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, null));

        assertThat(product.getBrand()).isNull();
        assertThat(product.getProductType()).isNull();
        assertThat(product.getProductClass()).isNull();
        assertThat(product.getProductGroup()).isNull();
        assertThat(product.getProductCategory()).isNull();
        assertThat(product.getProducer()).isNull();
        assertThat(product.getCountry()).isNull();
        assertThat(product.getDefaultUnit()).isNull();
        verifyNoInteractions(entityManager);
    }
}

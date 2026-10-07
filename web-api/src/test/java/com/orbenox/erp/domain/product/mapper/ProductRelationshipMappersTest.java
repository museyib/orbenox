package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.price.PriceList;
import com.orbenox.erp.domain.product.dto.*;
import com.orbenox.erp.domain.product.entity.*;
import com.orbenox.erp.domain.unit.Unit;
import com.orbenox.erp.domain.warehouse.Warehouse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRelationshipMappersTest {
    @Mock
    EntityManager entityManager;
    @InjectMocks
    ProductBarcodeMapperImpl barcodeMapper;
    @InjectMocks
    ProductUnitMapperImpl unitMapper;
    @InjectMocks
    ProductWarehouseMapperImpl warehouseMapper;
    @InjectMocks
    ProductPriceMapperImpl priceMapper;

    @Test
    void barcodeCreateMapsValuesAndEntityReferences() {
        Product product = new Product();
        Unit unit = new Unit();
        when(entityManager.getReference(Product.class, 10L)).thenReturn(product);
        when(entityManager.getReference(Unit.class, 20L)).thenReturn(unit);

        ProductBarcode barcode = barcodeMapper.toEntity(new ProductBarcodeCreateDto(10L, 20L, "012345"));

        assertThat(barcode.getProduct()).isSameAs(product);
        assertThat(barcode.getUnit()).isSameAs(unit);
        assertThat(barcode.getBarcode()).isEqualTo("012345");
        verify(entityManager).getReference(Product.class, 10L);
        verify(entityManager).getReference(Unit.class, 20L);
    }

    @Test
    void barcodeUpdateMapsReferencesAndPreservesNullFields() {
        ProductBarcode barcode = new ProductBarcode();
        barcode.setBarcode("old");
        barcode.setProduct(new Product());
        Product replacement = new Product();
        Unit unit = new Unit();
        when(entityManager.getReference(Product.class, 11L)).thenReturn(replacement);
        when(entityManager.getReference(Unit.class, 21L)).thenReturn(unit);

        barcodeMapper.updateEntityFromDto(new ProductBarcodeUpdateDto(1L, 11L, 21L, null), barcode);

        assertThat(barcode.getId()).isEqualTo(1L);
        assertThat(barcode.getProduct()).isSameAs(replacement);
        assertThat(barcode.getUnit()).isSameAs(unit);
        assertThat(barcode.getBarcode()).isEqualTo("old");
    }

    @Test
    void unitMapperMapsAssociationsAndFactorForCreateAndUpdate() {
        Product product = new Product();
        Unit unit = new Unit();
        BigDecimal factor = new BigDecimal("12.5");
        when(entityManager.getReference(Product.class, 12L)).thenReturn(product);
        when(entityManager.getReference(Unit.class, 22L)).thenReturn(unit);

        ProductUnit mapped = unitMapper.toEntity(new ProductUnitCreateDto(12L, 22L, factor));
        assertThat(mapped.getProduct()).isSameAs(product);
        assertThat(mapped.getUnit()).isSameAs(unit);
        assertThat(mapped.getFactorToBase()).isEqualByComparingTo(factor);

        ProductUnit existing = new ProductUnit();
        existing.setFactorToBase(BigDecimal.ONE);
        unitMapper.updateEntityFromDto(new ProductUnitUpdateDto(2L, 12L, 22L, factor), existing);
        assertThat(existing.getId()).isEqualTo(2L);
        assertThat(existing.getProduct()).isSameAs(product);
        assertThat(existing.getUnit()).isSameAs(unit);
        assertThat(existing.getFactorToBase()).isEqualByComparingTo(factor);
    }

    @Test
    void warehouseMapperMapsAssociationsAndQuantities() {
        Product product = new Product();
        Warehouse warehouse = new Warehouse();
        BigDecimal minimum = new BigDecimal("2.00");
        BigDecimal maximum = new BigDecimal("18.00");
        when(entityManager.getReference(Product.class, 13L)).thenReturn(product);
        when(entityManager.getReference(Warehouse.class, 23L)).thenReturn(warehouse);

        ProductWarehouse mapped = warehouseMapper.toEntity(
                new ProductWarehouseCreateDto(13L, 23L, minimum, maximum));
        assertThat(mapped.getProduct()).isSameAs(product);
        assertThat(mapped.getWarehouse()).isSameAs(warehouse);
        assertThat(mapped.getMinQuantity()).isEqualByComparingTo(minimum);
        assertThat(mapped.getMaxQuantity()).isEqualByComparingTo(maximum);

        ProductWarehouse existing = new ProductWarehouse();
        warehouseMapper.updateEntityFromDto(
                new ProductWarehouseUpdateDto(3L, 13L, 23L, minimum, maximum), existing);
        assertThat(existing.getId()).isEqualTo(3L);
        assertThat(existing.getProduct()).isSameAs(product);
        assertThat(existing.getWarehouse()).isSameAs(warehouse);
        assertThat(existing.getMinQuantity()).isEqualByComparingTo(minimum);
        assertThat(existing.getMaxQuantity()).isEqualByComparingTo(maximum);
    }

    @Test
    void priceMapperMapsValuesAndAllRelatedReferences() {
        Product product = new Product();
        PriceList priceList = new PriceList();
        Unit unit = new Unit();
        BigDecimal priceValue = new BigDecimal("25.99");
        BigDecimal factor = new BigDecimal("1.25");
        BigDecimal discount = new BigDecimal("0.15");
        Short rounding = 2;
        when(entityManager.getReference(Product.class, 14L)).thenReturn(product);
        when(entityManager.getReference(PriceList.class, 24L)).thenReturn(priceList);
        when(entityManager.getReference(Unit.class, 34L)).thenReturn(unit);

        ProductPrice mapped = priceMapper.toEntity(
                new ProductPriceCreateDto(14L, 24L, 34L, priceValue, factor, true, rounding, discount));
        assertThat(mapped.getProduct()).isSameAs(product);
        assertThat(mapped.getPriceList()).isSameAs(priceList);
        assertThat(mapped.getUnit()).isSameAs(unit);
        assertThat(mapped.getPrice()).isEqualByComparingTo(priceValue);
        assertThat(mapped.getFactorToParent()).isEqualByComparingTo(factor);
        assertThat(mapped.isFixedPrice()).isTrue();
        assertThat(mapped.getRoundLength()).isEqualTo(rounding);
        assertThat(mapped.getDiscountRatioLimit()).isEqualByComparingTo(discount);

        ProductPrice existing = new ProductPrice();
        priceMapper.updateEntityFromDto(
                new ProductPriceUpdateDto(4L, 14L, 24L, 34L, priceValue, factor, false, rounding, discount),
                existing);
        assertThat(existing.getId()).isEqualTo(4L);
        assertThat(existing.getProduct()).isSameAs(product);
        assertThat(existing.getPriceList()).isSameAs(priceList);
        assertThat(existing.getUnit()).isSameAs(unit);
        assertThat(existing.getPrice()).isEqualByComparingTo(priceValue);
        assertThat(existing.getFactorToParent()).isEqualByComparingTo(factor);
        assertThat(existing.isFixedPrice()).isFalse();
        assertThat(existing.getRoundLength()).isEqualTo(rounding);
        assertThat(existing.getDiscountRatioLimit()).isEqualByComparingTo(discount);
    }
}

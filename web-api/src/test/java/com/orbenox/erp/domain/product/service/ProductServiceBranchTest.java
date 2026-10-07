package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductCreateDto;
import com.orbenox.erp.domain.product.dto.ProductUpdateDto;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.product.mapper.ProductMapper;
import com.orbenox.erp.domain.product.projection.ProductBarcodeItem;
import com.orbenox.erp.domain.product.projection.ProductItem;
import com.orbenox.erp.domain.product.repository.ProductBarcodeRepository;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.unit.SimpleUnitItem;
import com.orbenox.erp.domain.unit.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceBranchTest {
    @Mock
    ProductRepository productRepository;
    @Mock
    ProductBarcodeRepository barcodeRepository;
    @Mock
    ProductMapper mapper;
    @InjectMocks
    ProductService service;

    @Test
    void getsAllOrSearchedProducts() {
        Slice<ProductItem> all = mock(Slice.class);
        Slice<ProductItem> searched = mock(Slice.class);
        when(productRepository.getAllItems(PageRequest.of(0, 10))).thenReturn(all);
        when(productRepository.getItemsSearched(PageRequest.of(1, 5), "widget")).thenReturn(searched);

        assertThat(service.getAllItems(0, 10, "")).isSameAs(all);
        assertThat(service.getAllItems(1, 5, "widget")).isSameAs(searched);
        verify(productRepository).getAllItems(PageRequest.of(0, 10));
        verify(productRepository).getItemsSearched(PageRequest.of(1, 5), "widget");
    }

    @Test
    void delegatesItemLookup() {
        ProductItem item = mock(ProductItem.class);
        when(productRepository.getItemById(1L)).thenReturn(item);
        assertThat(service.getItemById(1L)).isSameAs(item);
    }

    @Test
    void createAlsoRegistersDefaultBarcodeWithProductUnit() {
        ProductCreateDto dto = mock(ProductCreateDto.class);
        Unit unit = new Unit();
        Product product = product(unit, "001234");
        product.setId(2L);
        ProductItem item = mock(ProductItem.class);
        when(mapper.toEntity(dto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productRepository.getItemById(2L)).thenReturn(item);

        assertThat(service.create(dto)).isSameAs(item);

        ArgumentCaptor<ProductBarcode> barcodeCaptor = ArgumentCaptor.forClass(ProductBarcode.class);
        verify(barcodeRepository).save(barcodeCaptor.capture());
        assertThat(barcodeCaptor.getValue().getProduct()).isSameAs(product);
        assertThat(barcodeCaptor.getValue().getUnit()).isSameAs(unit);
        assertThat(barcodeCaptor.getValue().getBarcode()).isEqualTo("001234");
    }

    @Test
    void updateAddsBarcodeWhenNoBarcodeRecordExists() {
        Unit unit = new Unit();
        Product product = product(unit, "new-code");
        ProductUpdateDto dto = mock(ProductUpdateDto.class);
        ProductItem item = mock(ProductItem.class);
        when(productRepository.findByIdAndDeletedFalse(3L)).thenReturn(product);
        when(barcodeRepository.getItemByBarcode("new-code")).thenReturn(Optional.empty());
        when(productRepository.getItemById(3L)).thenReturn(item);

        assertThat(service.update(3L, dto)).isSameAs(item);

        ArgumentCaptor<ProductBarcode> captor = ArgumentCaptor.forClass(ProductBarcode.class);
        verify(barcodeRepository).save(captor.capture());
        assertThat(captor.getValue().getProduct()).isSameAs(product);
        assertThat(captor.getValue().getUnit()).isSameAs(unit);
        assertThat(captor.getValue().getBarcode()).isEqualTo("new-code");
        verify(barcodeRepository, never()).findByBarcode(anyString());
    }

    @Test
    void updateChangesExistingBarcodeUnitWhenDefaultUnitChanged() {
        Unit currentUnit = new Unit();
        Unit replacementUnit = new Unit();
        replacementUnit.setId(4L);
        Product product = product(replacementUnit, "same-code");
        ProductUpdateDto dto = mock(ProductUpdateDto.class);
        when(dto.defaultUnitId()).thenReturn(4L);
        ProductBarcodeItem barcodeItem = mock(ProductBarcodeItem.class);
        SimpleUnitItem oldBarcodeUnit = mock(SimpleUnitItem.class);
        when(oldBarcodeUnit.getId()).thenReturn(2L);
        when(barcodeItem.getUnit()).thenReturn(oldBarcodeUnit);
        ProductBarcode barcode = new ProductBarcode();
        barcode.setUnit(currentUnit);
        ProductItem item = mock(ProductItem.class);
        when(productRepository.findByIdAndDeletedFalse(4L)).thenReturn(product);
        when(barcodeRepository.getItemByBarcode("same-code")).thenReturn(Optional.of(barcodeItem));
        when(barcodeRepository.findByBarcode("same-code")).thenReturn(barcode);
        when(productRepository.getItemById(4L)).thenReturn(item);

        assertThat(service.update(4L, dto)).isSameAs(item);

        assertThat(barcode.getUnit()).isSameAs(replacementUnit);
        verify(barcodeRepository).findByBarcode("same-code");
        verify(barcodeRepository, never()).save(any(ProductBarcode.class));
    }

    @Test
    void updateLeavesBarcodeUntouchedWhenItsUnitAlreadyMatches() {
        Unit unit = new Unit();
        unit.setId(5L);
        Product product = product(unit, "same-code");
        ProductUpdateDto dto = mock(ProductUpdateDto.class);
        when(dto.defaultUnitId()).thenReturn(5L);
        ProductBarcodeItem barcodeItem = mock(ProductBarcodeItem.class);
        SimpleUnitItem barcodeUnit = mock(SimpleUnitItem.class);
        when(barcodeUnit.getId()).thenReturn(5L);
        when(barcodeItem.getUnit()).thenReturn(barcodeUnit);
        ProductItem item = mock(ProductItem.class);
        when(productRepository.findByIdAndDeletedFalse(5L)).thenReturn(product);
        when(barcodeRepository.getItemByBarcode("same-code")).thenReturn(Optional.of(barcodeItem));
        when(productRepository.getItemById(5L)).thenReturn(item);

        assertThat(service.update(5L, dto)).isSameAs(item);

        verify(barcodeRepository, never()).findByBarcode(anyString());
        verify(barcodeRepository, never()).save(any(ProductBarcode.class));
    }

    @Test
    void softDeleteMarksProductDeleted() {
        Product product = new Product();
        when(productRepository.findByIdAndDeletedFalse(6L)).thenReturn(product);

        service.softDelete(6L);

        assertThat(product.isDeleted()).isTrue();
        verify(barcodeRepository, never()).save(any(ProductBarcode.class));
    }

    private Product product(Unit unit, String barcode) {
        Product product = new Product();
        product.setDefaultUnit(unit);
        product.setDefaultBarcode(barcode);
        return product;
    }
}

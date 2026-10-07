package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductBarcodeCreateDto;
import com.orbenox.erp.domain.product.dto.ProductBarcodeUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.product.mapper.ProductBarcodeMapper;
import com.orbenox.erp.domain.product.projection.ProductBarcodeData;
import com.orbenox.erp.domain.product.projection.ProductBarcodeItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import com.orbenox.erp.domain.product.repository.ProductBarcodeRepository;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.request.UpdateProductBarcodeRequest;
import com.orbenox.erp.localization.LocalizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductBarcodeServiceTest {
    @Mock
    ProductBarcodeRepository repository;
    @Mock
    ProductRepository productRepository;
    @Mock
    ProductBarcodeMapper mapper;
    @Mock
    LocalizationService i18n;
    @InjectMocks
    ProductBarcodeService service;

    @Test
    void loadsBarcodeDataTogetherWithProduct() {
        SimpleProductItem product = mock(SimpleProductItem.class);
        List<ProductBarcodeItem> barcodes = List.of(mock(ProductBarcodeItem.class));
        when(productRepository.getSimpleItemById(1L)).thenReturn(product);
        when(repository.getItemsByProductId(1L)).thenReturn(barcodes);

        ProductBarcodeData data = service.getItemsByProductId(1L);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getBarcodes()).isSameAs(barcodes);
    }

    @Test
    void updatesInsertsDeletesAndUsesReturnedBarcodeProduct() {
        ProductBarcodeUpdateDto update = new ProductBarcodeUpdateDto(10L, 1L, 2L, "new");
        ProductBarcodeUpdateDto deletion = new ProductBarcodeUpdateDto(11L, 1L, 2L, "old");
        ProductBarcodeCreateDto insertion = new ProductBarcodeCreateDto(1L, 3L, "extra");
        UpdateProductBarcodeRequest request = request(1L, List.of(update), List.of(insertion), List.of(deletion));
        ProductBarcode current = new ProductBarcode();
        current.setId(10L);
        ProductBarcode inserted = new ProductBarcode();
        ProductBarcodeItem resultItem = mock(ProductBarcodeItem.class);
        SimpleProductItem product = mock(SimpleProductItem.class);
        when(repository.findAllById(List.of(10L))).thenReturn(List.of(current));
        when(mapper.toEntity(insertion)).thenReturn(inserted);
        when(repository.getItemsByProductId(1L)).thenReturn(List.of(resultItem));
        when(resultItem.getProduct()).thenReturn(product);

        ProductBarcodeData data = service.updateBarcodes(request);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getBarcodes()).containsExactly(resultItem);
        verify(mapper).updateEntityFromDto(update, current);
        verify(repository).saveAll(List.of(inserted, current));
        verify(repository).deleteAllById(List.of(11L));
        verify(productRepository, never()).getSimpleItemById(anyLong());
    }

    @Test
    void fallsBackToProductLookupWhenThereAreNoBarcodeItems() {
        UpdateProductBarcodeRequest request = request(2L, List.of(), List.of(), List.of());
        SimpleProductItem product = mock(SimpleProductItem.class);
        when(repository.findAllById(List.of())).thenReturn(List.of());
        when(repository.getItemsByProductId(2L)).thenReturn(List.of());
        when(productRepository.getSimpleItemById(2L)).thenReturn(product);

        ProductBarcodeData data = service.updateBarcodes(request);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getBarcodes()).isEmpty();
        verify(repository).saveAll(List.of());
        verify(repository).deleteAllById(List.of());
    }

    @Test
    void rejectsUpdateWhenBarcodeDoesNotExist() {
        ProductBarcodeUpdateDto update = new ProductBarcodeUpdateDto(12L, 1L, 2L, "missing");
        UpdateProductBarcodeRequest request = request(1L, List.of(update), List.of(), List.of());
        when(repository.findAllById(List.of(12L))).thenReturn(List.of());
        when(i18n.msg("barcode.notFound", "missing")).thenReturn("Barcode missing");

        assertThatThrownBy(() -> service.updateBarcodes(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Barcode missing");
        verify(repository, never()).saveAll(any());
        verify(repository, never()).deleteAllById(any());
    }

    private UpdateProductBarcodeRequest request(Long productId,
                                                List<ProductBarcodeUpdateDto> updates,
                                                List<ProductBarcodeCreateDto> inserts,
                                                List<ProductBarcodeUpdateDto> deletes) {
        UpdateProductBarcodeRequest request = new UpdateProductBarcodeRequest();
        request.setProductId(productId);
        request.setBarcodesToUpdate(updates);
        request.setBarcodesToInsert(inserts);
        request.setBarcodesToDelete(deletes);
        return request;
    }
}

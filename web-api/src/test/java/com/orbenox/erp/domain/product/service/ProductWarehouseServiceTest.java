package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductWarehouseCreateDto;
import com.orbenox.erp.domain.product.dto.ProductWarehouseUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import com.orbenox.erp.domain.product.mapper.ProductWarehouseMapper;
import com.orbenox.erp.domain.product.projection.ProductWarehouseData;
import com.orbenox.erp.domain.product.projection.ProductWarehouseItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.repository.ProductWarehouseRepository;
import com.orbenox.erp.domain.product.request.UpdateProductWarehouseRequest;
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
class ProductWarehouseServiceTest {
    @Mock
    ProductWarehouseRepository repository;
    @Mock
    ProductRepository productRepository;
    @Mock
    ProductWarehouseMapper mapper;
    @Mock
    LocalizationService i18n;
    @InjectMocks
    ProductWarehouseService service;

    @Test
    void loadsProductWithWarehouseItems() {
        SimpleProductItem product = mock(SimpleProductItem.class);
        List<ProductWarehouseItem> warehouses = List.of(mock(ProductWarehouseItem.class));
        when(productRepository.getSimpleItemById(1L)).thenReturn(product);
        when(repository.getItemsByProductId(1L)).thenReturn(warehouses);

        ProductWarehouseData data = service.getItemsByProductId(1L);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getWarehouses()).isSameAs(warehouses);
    }

    @Test
    void updatesInsertsDeletesAndUsesReturnedWarehouseProduct() {
        ProductWarehouseUpdateDto update = new ProductWarehouseUpdateDto(30L, 1L, 2L, null, null);
        ProductWarehouseUpdateDto deletion = new ProductWarehouseUpdateDto(31L, 1L, 3L, null, null);
        ProductWarehouseCreateDto insertion = new ProductWarehouseCreateDto(1L, 4L, null, null);
        UpdateProductWarehouseRequest request = request(1L, List.of(update), List.of(insertion), List.of(deletion));
        ProductWarehouse current = new ProductWarehouse();
        current.setId(30L);
        ProductWarehouse inserted = new ProductWarehouse();
        ProductWarehouseItem resultItem = mock(ProductWarehouseItem.class);
        SimpleProductItem product = mock(SimpleProductItem.class);
        when(repository.findAllById(List.of(30L))).thenReturn(List.of(current));
        when(mapper.toEntity(insertion)).thenReturn(inserted);
        when(repository.getItemsByProductId(1L)).thenReturn(List.of(resultItem));
        when(resultItem.getProduct()).thenReturn(product);

        ProductWarehouseData data = service.updateProductWarehouses(request);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getWarehouses()).containsExactly(resultItem);
        verify(mapper).updateEntityFromDto(update, current);
        verify(repository).saveAll(List.of(inserted, current));
        verify(repository).deleteAllById(List.of(31L));
        verify(productRepository, never()).getSimpleItemById(anyLong());
    }

    @Test
    void fallsBackToProductLookupWhenNoWarehouseItemsRemain() {
        UpdateProductWarehouseRequest request = request(2L, List.of(), List.of(), List.of());
        SimpleProductItem product = mock(SimpleProductItem.class);
        when(repository.findAllById(List.of())).thenReturn(List.of());
        when(repository.getItemsByProductId(2L)).thenReturn(List.of());
        when(productRepository.getSimpleItemById(2L)).thenReturn(product);

        ProductWarehouseData data = service.updateProductWarehouses(request);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getWarehouses()).isEmpty();
        verify(repository).saveAll(List.of());
        verify(repository).deleteAllById(List.of());
    }

    @Test
    void rejectsUpdateForUnknownWarehouseAssociation() {
        ProductWarehouseUpdateDto update = new ProductWarehouseUpdateDto(32L, 1L, 9L, null, null);
        UpdateProductWarehouseRequest request = request(1L, List.of(update), List.of(), List.of());
        when(repository.findAllById(List.of(32L))).thenReturn(List.of());
        when(i18n.msg("warehouse.notFound", 9L)).thenReturn("Warehouse association missing");

        assertThatThrownBy(() -> service.updateProductWarehouses(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Warehouse association missing");
        verify(repository, never()).saveAll(any());
    }

    private UpdateProductWarehouseRequest request(Long productId,
                                                  List<ProductWarehouseUpdateDto> updates,
                                                  List<ProductWarehouseCreateDto> inserts,
                                                  List<ProductWarehouseUpdateDto> deletes) {
        UpdateProductWarehouseRequest request = new UpdateProductWarehouseRequest();
        request.setProductId(productId);
        request.setWarehousesToUpdate(updates);
        request.setWarehousesToInsert(inserts);
        request.setWarehousesToDelete(deletes);
        return request;
    }
}

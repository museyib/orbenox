package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductUnitCreateDto;
import com.orbenox.erp.domain.product.dto.ProductUnitUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductUnit;
import com.orbenox.erp.domain.product.mapper.ProductUnitMapper;
import com.orbenox.erp.domain.product.projection.ProductUnitData;
import com.orbenox.erp.domain.product.projection.ProductUnitItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.repository.ProductUnitRepository;
import com.orbenox.erp.domain.product.request.UpdateProductUnitRequest;
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
class ProductUnitServiceTest {
    @Mock
    ProductUnitRepository repository;
    @Mock
    ProductRepository productRepository;
    @Mock
    ProductUnitMapper mapper;
    @Mock
    LocalizationService i18n;
    @InjectMocks
    ProductUnitService service;

    @Test
    void loadsProductWithItsUnitItems() {
        SimpleProductItem product = mock(SimpleProductItem.class);
        List<ProductUnitItem> units = List.of(mock(ProductUnitItem.class));
        when(productRepository.getSimpleItemById(1L)).thenReturn(product);
        when(repository.getItemsByProductId(1L)).thenReturn(units);

        ProductUnitData data = service.getItemsByProductId(1L);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getUnits()).isSameAs(units);
    }

    @Test
    void updatesInsertsDeletesAndUsesReturnedUnitProduct() {
        ProductUnitUpdateDto update = new ProductUnitUpdateDto(20L, 1L, 2L, null);
        ProductUnitUpdateDto deletion = new ProductUnitUpdateDto(21L, 1L, 3L, null);
        ProductUnitCreateDto insertion = new ProductUnitCreateDto(1L, 4L, null);
        UpdateProductUnitRequest request = request(1L, List.of(update), List.of(insertion), List.of(deletion));
        ProductUnit current = new ProductUnit();
        current.setId(20L);
        ProductUnit inserted = new ProductUnit();
        ProductUnitItem resultItem = mock(ProductUnitItem.class);
        SimpleProductItem product = mock(SimpleProductItem.class);
        when(repository.findAllById(List.of(20L))).thenReturn(List.of(current));
        when(mapper.toEntity(insertion)).thenReturn(inserted);
        when(repository.getItemsByProductId(1L)).thenReturn(List.of(resultItem));
        when(resultItem.getProduct()).thenReturn(product);

        ProductUnitData data = service.updateProductUnits(request);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getUnits()).containsExactly(resultItem);
        verify(mapper).updateEntityFromDto(update, current);
        verify(repository).saveAll(List.of(inserted, current));
        verify(repository).deleteAllById(List.of(21L));
        verify(productRepository, never()).getSimpleItemById(anyLong());
    }

    @Test
    void fallsBackToProductLookupWhenNoUnitsRemain() {
        UpdateProductUnitRequest request = request(2L, List.of(), List.of(), List.of());
        SimpleProductItem product = mock(SimpleProductItem.class);
        when(repository.findAllById(List.of())).thenReturn(List.of());
        when(repository.getItemsByProductId(2L)).thenReturn(List.of());
        when(productRepository.getSimpleItemById(2L)).thenReturn(product);

        ProductUnitData data = service.updateProductUnits(request);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getUnits()).isEmpty();
        verify(repository).saveAll(List.of());
        verify(repository).deleteAllById(List.of());
    }

    @Test
    void rejectsUpdateForUnknownUnitAssociation() {
        ProductUnitUpdateDto update = new ProductUnitUpdateDto(22L, 1L, 7L, null);
        UpdateProductUnitRequest request = request(1L, List.of(update), List.of(), List.of());
        when(repository.findAllById(List.of(22L))).thenReturn(List.of());
        when(i18n.msg("unit.notFound", 7L)).thenReturn("Unit association missing");

        assertThatThrownBy(() -> service.updateProductUnits(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unit association missing");
        verify(repository, never()).saveAll(any());
    }

    private UpdateProductUnitRequest request(Long productId,
                                             List<ProductUnitUpdateDto> updates,
                                             List<ProductUnitCreateDto> inserts,
                                             List<ProductUnitUpdateDto> deletes) {
        UpdateProductUnitRequest request = new UpdateProductUnitRequest();
        request.setProductId(productId);
        request.setUnitsToUpdate(updates);
        request.setUnitsToInsert(inserts);
        request.setUnitsToDelete(deletes);
        return request;
    }
}

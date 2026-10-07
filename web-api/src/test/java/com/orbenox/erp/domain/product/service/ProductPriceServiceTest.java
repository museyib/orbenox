package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductPriceCreateDto;
import com.orbenox.erp.domain.product.dto.ProductPriceUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductPrice;
import com.orbenox.erp.domain.product.mapper.ProductPriceMapper;
import com.orbenox.erp.domain.product.projection.ProductPriceItem;
import com.orbenox.erp.domain.product.projection.ProductPricingData;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import com.orbenox.erp.domain.product.repository.ProductPriceRepository;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.request.UpdateProductPriceRequest;
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
class ProductPriceServiceTest {
    @Mock
    ProductPriceRepository repository;
    @Mock
    ProductRepository productRepository;
    @Mock
    ProductPriceMapper mapper;
    @Mock
    LocalizationService i18n;
    @InjectMocks
    ProductPriceService service;

    @Test
    void getsPricingDataWithProductAndPriceList() {
        SimpleProductItem product = mock(SimpleProductItem.class);
        List<ProductPriceItem> prices = List.of(mock(ProductPriceItem.class));
        when(productRepository.getSimpleItemById(1L)).thenReturn(product);
        when(repository.getItemsByProductId(1L)).thenReturn(prices);

        ProductPricingData data = service.getPriceDataByProductId(1L);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getPrices()).isSameAs(prices);
    }

    @Test
    void updatesAndInsertsPricesThenUsesProductFromReturnedPriceRows() {
        ProductPriceUpdateDto update = new ProductPriceUpdateDto(40L, 1L, 2L, 3L,
                null, null, false, null, null);
        ProductPriceCreateDto insertion = new ProductPriceCreateDto(1L, 4L, 3L,
                null, null, false, null, null);
        UpdateProductPriceRequest request = request(1L, List.of(update), List.of(insertion));
        ProductPrice current = new ProductPrice();
        current.setId(40L);
        ProductPrice inserted = new ProductPrice();
        ProductPriceItem row = mock(ProductPriceItem.class);
        SimpleProductItem product = mock(SimpleProductItem.class);
        when(repository.findAllById(List.of(40L))).thenReturn(List.of(current));
        when(mapper.toEntity(insertion)).thenReturn(inserted);
        when(repository.getItemsByProductId(1L)).thenReturn(List.of(row));
        when(row.getProduct()).thenReturn(product);

        ProductPricingData data = service.updateProductPrices(request);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getPrices()).containsExactly(row);
        verify(mapper).updateEntityFromDto(update, current);
        verify(repository).saveAll(List.of(current, inserted));
        verify(productRepository, never()).getSimpleItemById(anyLong());
    }

    @Test
    void loadsProductDirectlyWhenNoPriceRowsAreReturned() {
        UpdateProductPriceRequest request = request(2L, List.of(), List.of());
        SimpleProductItem product = mock(SimpleProductItem.class);
        when(repository.findAllById(List.of())).thenReturn(List.of());
        when(repository.getItemsByProductId(2L)).thenReturn(List.of());
        when(productRepository.getSimpleItemById(2L)).thenReturn(product);

        ProductPricingData data = service.updateProductPrices(request);

        assertThat(data.getProduct()).isSameAs(product);
        assertThat(data.getPrices()).isEmpty();
        verify(repository).saveAll(List.of());
    }

    @Test
    void rejectsUpdateForMissingPriceAssociation() {
        ProductPriceUpdateDto update = new ProductPriceUpdateDto(41L, 1L, 8L, 3L,
                null, null, false, null, null);
        UpdateProductPriceRequest request = request(1L, List.of(update), List.of());
        when(repository.findAllById(List.of(41L))).thenReturn(List.of());
        when(i18n.msg("priceList.notFound", 8L)).thenReturn("Price list missing");

        assertThatThrownBy(() -> service.updateProductPrices(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price list missing");
        verify(repository, never()).saveAll(any());
    }

    @Test
    void delegatesLookupByProductPriceListAndUnit() {
        ProductPriceItem item = mock(ProductPriceItem.class);
        when(repository.getByProductIdAndPriceListId(3L, 4L, 5L)).thenReturn(item);
        assertThat(service.getByProductIdAndPriceListId(3L, 4L, 5L)).isSameAs(item);
    }

    private UpdateProductPriceRequest request(Long productId,
                                              List<ProductPriceUpdateDto> updates,
                                              List<ProductPriceCreateDto> inserts) {
        UpdateProductPriceRequest request = new UpdateProductPriceRequest();
        request.setProductId(productId);
        request.setPriceListToUpdate(updates);
        request.setPriceListToInsert(inserts);
        return request;
    }
}

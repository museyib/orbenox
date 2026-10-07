package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.domain.product.projection.ProductPriceItem;
import com.orbenox.erp.domain.product.projection.ProductPricingData;
import com.orbenox.erp.domain.product.request.UpdateProductPriceRequest;
import com.orbenox.erp.domain.product.service.ProductPriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductPriceControllerTest {
    @Mock
    ProductPriceService service;
    @InjectMocks
    ProductPriceController controller;

    @Test
    void getsAndUpdatesProductPricing() {
        ProductPricingData data = mock(ProductPricingData.class);
        UpdateProductPriceRequest request = new UpdateProductPriceRequest();
        when(service.getPriceDataByProductId(3L)).thenReturn(data);
        when(service.updateProductPrices(request)).thenReturn(data);

        assertThat(controller.getAllByProductId(3L).getBody().getData()).isSameAs(data);
        assertThat(controller.updateProductPrices(request).getBody().getData()).isSameAs(data);
        verify(service).getPriceDataByProductId(3L);
        verify(service).updateProductPrices(request);
    }

    @Test
    void getByProductPriceListAndUnitReturnsServiceResult() {
        ProductPriceItem item = mock(ProductPriceItem.class);
        when(service.getByProductIdAndPriceListId(4L, 5L, 6L)).thenReturn(item);

        assertThat(controller.getByProductIdAndPriceListId(4L, 5L, 6L).getBody().getData()).isSameAs(item);
        verify(service).getByProductIdAndPriceListId(4L, 5L, 6L);
    }
}

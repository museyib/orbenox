package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.domain.product.projection.ProductBarcodeData;
import com.orbenox.erp.domain.product.request.UpdateProductBarcodeRequest;
import com.orbenox.erp.domain.product.service.ProductBarcodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductBarcodeControllerTest {
    @Mock
    ProductBarcodeService service;
    @InjectMocks
    ProductBarcodeController controller;

    @Test
    void getBarcodesReturnsServiceData() {
        ProductBarcodeData data = mock(ProductBarcodeData.class);
        when(service.getItemsByProductId(2L)).thenReturn(data);
        assertThat(controller.getProductBarcodesByProductId(2L).getBody().getData()).isSameAs(data);
        verify(service).getItemsByProductId(2L);
    }

    @Test
    void updateBarcodesPassesRequestAndReturnsUpdatedData() {
        UpdateProductBarcodeRequest request = new UpdateProductBarcodeRequest();
        ProductBarcodeData data = mock(ProductBarcodeData.class);
        when(service.updateBarcodes(request)).thenReturn(data);
        assertThat(controller.updateProductBarcodes(request).getBody().getData()).isSameAs(data);
        verify(service).updateBarcodes(request);
    }
}

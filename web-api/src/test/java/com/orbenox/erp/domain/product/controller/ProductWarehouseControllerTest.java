package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.domain.product.projection.ProductWarehouseData;
import com.orbenox.erp.domain.product.request.UpdateProductWarehouseRequest;
import com.orbenox.erp.domain.product.service.ProductWarehouseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductWarehouseControllerTest {
    @Mock
    ProductWarehouseService service;
    @InjectMocks
    ProductWarehouseController controller;

    @Test
    void getsWarehousesForProduct() {
        ProductWarehouseData data = mock(ProductWarehouseData.class);
        when(service.getItemsByProductId(10L)).thenReturn(data);
        assertThat(controller.getItemsByProductId(10L).getBody().getData()).isSameAs(data);
        verify(service).getItemsByProductId(10L);
    }

    @Test
    void updatesProductWarehouses() {
        UpdateProductWarehouseRequest request = new UpdateProductWarehouseRequest();
        ProductWarehouseData data = mock(ProductWarehouseData.class);
        when(service.updateProductWarehouses(request)).thenReturn(data);
        assertThat(controller.updateProductWarehouses(request).getBody().getData()).isSameAs(data);
        verify(service).updateProductWarehouses(request);
    }
}

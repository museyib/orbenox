package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.domain.product.projection.ProductUnitData;
import com.orbenox.erp.domain.product.request.UpdateProductUnitRequest;
import com.orbenox.erp.domain.product.service.ProductUnitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUnitControllerTest {
    @Mock
    ProductUnitService service;
    @InjectMocks
    ProductUnitController controller;

    @Test
    void getsUnitsForProduct() {
        ProductUnitData data = mock(ProductUnitData.class);
        when(service.getItemsByProductId(9L)).thenReturn(data);
        assertThat(controller.getItemsByProductId(9L).getBody().getData()).isSameAs(data);
        verify(service).getItemsByProductId(9L);
    }

    @Test
    void updatesProductUnits() {
        UpdateProductUnitRequest request = new UpdateProductUnitRequest();
        ProductUnitData data = mock(ProductUnitData.class);
        when(service.updateProductUnits(request)).thenReturn(data);
        assertThat(controller.updateProductUnits(request).getBody().getData()).isSameAs(data);
        verify(service).updateProductUnits(request);
    }
}

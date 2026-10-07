package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.domain.product.projection.ProductItem;
import com.orbenox.erp.domain.product.service.ProductService;
import com.orbenox.erp.localization.LocalizationService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    private final ProductService productService = mock(ProductService.class);
    private final LocalizationService localizationService = mock(LocalizationService.class);
    private final ProductController controller = new ProductController(productService, localizationService);

    @Test
    void getAll_shouldReturnItemsAndPaginationHeaders() {
        ProductItem product = mock(ProductItem.class);
        when(productService.getAllItems(1, 4, "tea")).thenReturn(new SliceImpl<>(
                List.of(product),
                PageRequest.of(1, 4),
                true));

        var response = controller.getAll(1, 4, "tea");

        assertThat(response.getBody().getData()).containsExactly(product);
        assertThat(response.getBody().getHeaders())
                .containsEntry("hasNext", true)
                .containsEntry("hasPrev", true);
    }

    @Test
    void getById_shouldReturnProductItem() {
        ProductItem product = mock(ProductItem.class);
        when(productService.getItemById(9L)).thenReturn(product);

        assertThat(controller.getById(9L).getBody().getData()).isSameAs(product);
    }

    @Test
    void create_shouldReturnCreatedProduct() {
        ProductItem product = mock(ProductItem.class);
        when(productService.create(null)).thenReturn(product);

        assertThat(controller.create(null).getBody().getData()).isSameAs(product);
    }

    @Test
    void update_shouldReturnUpdatedProduct() {
        ProductItem product = mock(ProductItem.class);
        when(productService.update(9L, null)).thenReturn(product);

        assertThat(controller.update(9L, null).getBody().getData()).isSameAs(product);
    }

    @Test
    void delete_shouldSoftDeleteAndReturnLocalizedMessage() {
        when(localizationService.msg("product.deleted", 9L)).thenReturn("Product deleted");

        var response = controller.delete(9L);

        verify(productService).softDelete(9L);
        assertThat(response.getBody().getMessage()).isEqualTo("Product deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("product.deleted");
    }
}

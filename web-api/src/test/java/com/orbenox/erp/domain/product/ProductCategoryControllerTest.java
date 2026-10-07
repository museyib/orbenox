package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.domain.product.dto.ProductCategoryCreateDto;
import com.orbenox.erp.domain.product.dto.ProductCategoryUpdateDto;
import com.orbenox.erp.domain.product.projection.ProductCategoryItem;
import com.orbenox.erp.domain.product.service.ProductCategoryService;
import com.orbenox.erp.localization.LocalizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCategoryControllerTest {
    @Mock
    private ProductCategoryService productCategoryService;
    @Mock
    private LocalizationService i18n;
    @InjectMocks
    private ProductCategoryController controller;

    @Test
    void getAllReturnsPageContentAndNavigationHeaders() {
        ProductCategoryItem item = mock(ProductCategoryItem.class);
        Slice<ProductCategoryItem> slice = new SliceImpl<>(List.of(item), PageRequest.of(2, 5), true);
        when(productCategoryService.getAllItems(2, 5, "match")).thenReturn(slice);
        var response = controller.getAll(2, 5, "match");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).containsExactly(item);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", true).containsEntry("hasPrev", true);
        verify(productCategoryService).getAllItems(2, 5, "match");
    }

    @Test
    void getByIdCreateAndUpdateReturnServiceResults() {
        ProductCategoryItem item = mock(ProductCategoryItem.class);
        ProductCategoryCreateDto createDto = mock(ProductCategoryCreateDto.class);
        ProductCategoryUpdateDto updateDto = mock(ProductCategoryUpdateDto.class);
        when(productCategoryService.getItemById(17L)).thenReturn(item);
        when(productCategoryService.create(createDto)).thenReturn(item);
        when(productCategoryService.update(17L, updateDto)).thenReturn(item);

        assertThat(controller.getById(17L).getBody().getData()).isSameAs(item);
        assertThat(controller.create(createDto).getBody().getData()).isSameAs(item);
        assertThat(controller.update(17L, updateDto).getBody().getData()).isSameAs(item);
        verify(productCategoryService).getItemById(17L);
        verify(productCategoryService).create(createDto);
        verify(productCategoryService).update(17L, updateDto);
    }

    @Test
    void deleteInvokesServiceAndReturnsLocalizedMessage() {
        when(i18n.msg("productCategory.deleted", 17L)).thenReturn("Deleted");
        var response = controller.delete(17L);

        verify(productCategoryService).softDelete(17L);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("productCategory.deleted");
    }
}

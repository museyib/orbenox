package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.domain.product.dto.ProductTypeCreateDto;
import com.orbenox.erp.domain.product.dto.ProductTypeUpdateDto;
import com.orbenox.erp.domain.product.projection.ProductTypeItem;
import com.orbenox.erp.domain.product.service.ProductTypeService;
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
class ProductTypeControllerTest {
    @Mock
    private ProductTypeService productTypeService;
    @Mock
    private LocalizationService i18n;
    @InjectMocks
    private ProductTypeController controller;

    @Test
    void getAllReturnsPageContentAndNavigationHeaders() {
        ProductTypeItem item = mock(ProductTypeItem.class);
        Slice<ProductTypeItem> slice = new SliceImpl<>(List.of(item), PageRequest.of(2, 5), true);
        when(productTypeService.getAllItems(2, 5, "match")).thenReturn(slice);
        var response = controller.getAll(2, 5, "match");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).containsExactly(item);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", true).containsEntry("hasPrev", true);
        verify(productTypeService).getAllItems(2, 5, "match");
    }

    @Test
    void getByIdCreateAndUpdateReturnServiceResults() {
        ProductTypeItem item = mock(ProductTypeItem.class);
        ProductTypeCreateDto createDto = mock(ProductTypeCreateDto.class);
        ProductTypeUpdateDto updateDto = mock(ProductTypeUpdateDto.class);
        when(productTypeService.getItemById(17L)).thenReturn(item);
        when(productTypeService.create(createDto)).thenReturn(item);
        when(productTypeService.update(17L, updateDto)).thenReturn(item);

        assertThat(controller.getById(17L).getBody().getData()).isSameAs(item);
        assertThat(controller.create(createDto).getBody().getData()).isSameAs(item);
        assertThat(controller.update(17L, updateDto).getBody().getData()).isSameAs(item);
        verify(productTypeService).getItemById(17L);
        verify(productTypeService).create(createDto);
        verify(productTypeService).update(17L, updateDto);
    }

    @Test
    void deleteInvokesServiceAndReturnsLocalizedMessage() {
        when(i18n.msg("productType.deleted", 17L)).thenReturn("Deleted");
        var response = controller.delete(17L);

        verify(productTypeService).softDelete(17L);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("productType.deleted");
    }
}

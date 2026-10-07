package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.domain.product.dto.ProductClassCreateDto;
import com.orbenox.erp.domain.product.dto.ProductClassUpdateDto;
import com.orbenox.erp.domain.product.projection.ProductClassItem;
import com.orbenox.erp.domain.product.service.ProductClassService;
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
class ProductClassControllerTest {
    @Mock
    private ProductClassService productClassService;
    @Mock
    private LocalizationService i18n;
    @InjectMocks
    private ProductClassController controller;

    @Test
    void getAllReturnsPageContentAndNavigationHeaders() {
        ProductClassItem item = mock(ProductClassItem.class);
        Slice<ProductClassItem> slice = new SliceImpl<>(List.of(item), PageRequest.of(2, 5), true);
        when(productClassService.getAllItems(2, 5, "match")).thenReturn(slice);
        var response = controller.getAll(2, 5, "match");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).containsExactly(item);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", true).containsEntry("hasPrev", true);
        verify(productClassService).getAllItems(2, 5, "match");
    }

    @Test
    void getByIdCreateAndUpdateReturnServiceResults() {
        ProductClassItem item = mock(ProductClassItem.class);
        ProductClassCreateDto createDto = mock(ProductClassCreateDto.class);
        ProductClassUpdateDto updateDto = mock(ProductClassUpdateDto.class);
        when(productClassService.getItemById(17L)).thenReturn(item);
        when(productClassService.create(createDto)).thenReturn(item);
        when(productClassService.update(17L, updateDto)).thenReturn(item);

        assertThat(controller.getById(17L).getBody().getData()).isSameAs(item);
        assertThat(controller.create(createDto).getBody().getData()).isSameAs(item);
        assertThat(controller.update(17L, updateDto).getBody().getData()).isSameAs(item);
        verify(productClassService).getItemById(17L);
        verify(productClassService).create(createDto);
        verify(productClassService).update(17L, updateDto);
    }

    @Test
    void deleteInvokesServiceAndReturnsLocalizedMessage() {
        when(i18n.msg("productClass.deleted", 17L)).thenReturn("Deleted");
        var response = controller.delete(17L);

        verify(productClassService).softDelete(17L);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("productClass.deleted");
    }
}

package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.domain.product.dto.BrandCreateDto;
import com.orbenox.erp.domain.product.dto.BrandUpdateDto;
import com.orbenox.erp.domain.product.projection.BrandItem;
import com.orbenox.erp.domain.product.service.BrandService;
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
class BrandControllerTest {
    @Mock
    private BrandService brandService;
    @Mock
    private LocalizationService i18n;
    @InjectMocks
    private BrandController controller;

    @Test
    void getAllReturnsPageContentAndNavigationHeaders() {
        BrandItem item = mock(BrandItem.class);
        Slice<BrandItem> slice = new SliceImpl<>(List.of(item), PageRequest.of(2, 5), true);
        when(brandService.getAllItems(2, 5, "match")).thenReturn(slice);
        var response = controller.getAll(2, 5, "match");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).containsExactly(item);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", true).containsEntry("hasPrev", true);
        verify(brandService).getAllItems(2, 5, "match");
    }

    @Test
    void getByIdCreateAndUpdateReturnServiceResults() {
        BrandItem item = mock(BrandItem.class);
        BrandCreateDto createDto = mock(BrandCreateDto.class);
        BrandUpdateDto updateDto = mock(BrandUpdateDto.class);
        when(brandService.getItemById(17L)).thenReturn(item);
        when(brandService.create(createDto)).thenReturn(item);
        when(brandService.update(17L, updateDto)).thenReturn(item);

        assertThat(controller.getById(17L).getBody().getData()).isSameAs(item);
        assertThat(controller.create(createDto).getBody().getData()).isSameAs(item);
        assertThat(controller.update(17L, updateDto).getBody().getData()).isSameAs(item);
        verify(brandService).getItemById(17L);
        verify(brandService).create(createDto);
        verify(brandService).update(17L, updateDto);
    }

    @Test
    void deleteInvokesServiceAndReturnsLocalizedMessage() {
        when(i18n.msg("brand.deleted", 17L)).thenReturn("Deleted");
        var response = controller.delete(17L);

        verify(brandService).softDelete(17L);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("brand.deleted");
    }
}

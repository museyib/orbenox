package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.domain.product.dto.ProductGroupCreateDto;
import com.orbenox.erp.domain.product.dto.ProductGroupUpdateDto;
import com.orbenox.erp.domain.product.projection.ProductGroupItem;
import com.orbenox.erp.domain.product.service.ProductGroupService;
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
class ProductGroupControllerTest {
    @Mock
    private ProductGroupService productGroupService;
    @Mock
    private LocalizationService i18n;
    @InjectMocks
    private ProductGroupController controller;

    @Test
    void getAllReturnsPageContentAndNavigationHeaders() {
        ProductGroupItem item = mock(ProductGroupItem.class);
        Slice<ProductGroupItem> slice = new SliceImpl<>(List.of(item), PageRequest.of(2, 5), true);
        when(productGroupService.getAllItems(2, 5, "match")).thenReturn(slice);
        var response = controller.getAll(2, 5, "match");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).containsExactly(item);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", true).containsEntry("hasPrev", true);
        verify(productGroupService).getAllItems(2, 5, "match");
    }

    @Test
    void getByIdCreateAndUpdateReturnServiceResults() {
        ProductGroupItem item = mock(ProductGroupItem.class);
        ProductGroupCreateDto createDto = mock(ProductGroupCreateDto.class);
        ProductGroupUpdateDto updateDto = mock(ProductGroupUpdateDto.class);
        when(productGroupService.getItemById(17L)).thenReturn(item);
        when(productGroupService.create(createDto)).thenReturn(item);
        when(productGroupService.update(17L, updateDto)).thenReturn(item);

        assertThat(controller.getById(17L).getBody().getData()).isSameAs(item);
        assertThat(controller.create(createDto).getBody().getData()).isSameAs(item);
        assertThat(controller.update(17L, updateDto).getBody().getData()).isSameAs(item);
        verify(productGroupService).getItemById(17L);
        verify(productGroupService).create(createDto);
        verify(productGroupService).update(17L, updateDto);
    }

    @Test
    void getExcludedReturnsServiceGroups() {
        var groups = List.of(mock(com.orbenox.erp.domain.product.projection.SimpleProductGroupItem.class));
        when(productGroupService.findAllExcluded(17L)).thenReturn(groups);

        assertThat(controller.getAllExcluded(17L).getBody().getData()).isSameAs(groups);
        verify(productGroupService).findAllExcluded(17L);
    }

    @Test
    void deleteInvokesServiceAndReturnsLocalizedMessage() {
        when(i18n.msg("productGroup.deleted", 17L)).thenReturn("Deleted");
        var response = controller.delete(17L);

        verify(productGroupService).softDelete(17L);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("productGroup.deleted");
    }
}

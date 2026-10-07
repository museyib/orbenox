package com.orbenox.erp.domain.resource;

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
class ResourceControllerTest {
    @Mock
    private ResourceService resourceService;
    @Mock
    private LocalizationService i18n;
    @InjectMocks
    private ResourceController controller;

    @Test
    void getAllReturnsPageContentAndNavigationHeaders() {
        ResourceItem item = mock(ResourceItem.class);
        Slice<ResourceItem> slice = new SliceImpl<>(List.of(item), PageRequest.of(2, 5), true);
        when(resourceService.getAllItems(2, 5, "match")).thenReturn(slice);
        var response = controller.getActions(2, 5, "match");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).containsExactly(item);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", true).containsEntry("hasPrev", true);
        verify(resourceService).getAllItems(2, 5, "match");
    }

    @Test
    void getByIdCreateAndUpdateReturnServiceResults() {
        ResourceData item = mock(ResourceData.class);
        ResourceItem mutationResult = mock(ResourceItem.class);
        ResourceCreateDto createDto = mock(ResourceCreateDto.class);
        ResourceUpdateDto updateDto = mock(ResourceUpdateDto.class);
        when(resourceService.getItemById(17L)).thenReturn(item);
        when(resourceService.create(createDto)).thenReturn(mutationResult);
        when(resourceService.update(17L, updateDto)).thenReturn(mutationResult);

        assertThat(controller.getById(17L).getBody().getData()).isSameAs(item);
        assertThat(controller.create(createDto).getBody().getData()).isSameAs(mutationResult);
        assertThat(controller.update(17L, updateDto).getBody().getData()).isSameAs(mutationResult);
        verify(resourceService).getItemById(17L);
        verify(resourceService).create(createDto);
        verify(resourceService).update(17L, updateDto);
    }

    @Test
    void deleteInvokesServiceAndReturnsLocalizedMessage() {
        when(i18n.msg("resource.deleted", 17L)).thenReturn("Deleted");
        var response = controller.delete(17L);

        verify(resourceService).softDelete(17L);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("resource.deleted");
    }
}

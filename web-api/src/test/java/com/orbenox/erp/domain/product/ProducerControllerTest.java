package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.domain.product.dto.ProducerCreateDto;
import com.orbenox.erp.domain.product.dto.ProducerUpdateDto;
import com.orbenox.erp.domain.product.projection.ProducerItem;
import com.orbenox.erp.domain.product.service.ProducerService;
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
class ProducerControllerTest {
    @Mock
    private ProducerService producerService;
    @Mock
    private LocalizationService i18n;
    @InjectMocks
    private ProducerController controller;

    @Test
    void getAllReturnsPageContentAndNavigationHeaders() {
        ProducerItem item = mock(ProducerItem.class);
        Slice<ProducerItem> slice = new SliceImpl<>(List.of(item), PageRequest.of(2, 5), true);
        when(producerService.getAllItems(2, 5, "match")).thenReturn(slice);
        var response = controller.getAll(2, 5, "match");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).containsExactly(item);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", true).containsEntry("hasPrev", true);
        verify(producerService).getAllItems(2, 5, "match");
    }

    @Test
    void getByIdCreateAndUpdateReturnServiceResults() {
        ProducerItem item = mock(ProducerItem.class);
        ProducerCreateDto createDto = mock(ProducerCreateDto.class);
        ProducerUpdateDto updateDto = mock(ProducerUpdateDto.class);
        when(producerService.getItemById(17L)).thenReturn(item);
        when(producerService.create(createDto)).thenReturn(item);
        when(producerService.update(17L, updateDto)).thenReturn(item);

        assertThat(controller.getById(17L).getBody().getData()).isSameAs(item);
        assertThat(controller.create(createDto).getBody().getData()).isSameAs(item);
        assertThat(controller.update(17L, updateDto).getBody().getData()).isSameAs(item);
        verify(producerService).getItemById(17L);
        verify(producerService).create(createDto);
        verify(producerService).update(17L, updateDto);
    }

    @Test
    void deleteInvokesServiceAndReturnsLocalizedMessage() {
        when(i18n.msg("producer.deleted", 17L)).thenReturn("Deleted");
        var response = controller.delete(17L);

        verify(producerService).softDelete(17L);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("producer.deleted");
    }
}

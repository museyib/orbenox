package com.orbenox.erp.domain.currency;

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
class CurrencyControllerTest {
    @Mock
    private CurrencyService currencyService;
    @Mock
    private LocalizationService i18n;
    @InjectMocks
    private CurrencyController controller;

    @Test
    void getAllReturnsPageContentAndNavigationHeaders() {
        CurrencyItem item = mock(CurrencyItem.class);
        Slice<CurrencyItem> slice = new SliceImpl<>(List.of(item), PageRequest.of(2, 5), true);
        when(currencyService.getAllItems(2, 5, "match")).thenReturn(slice);
        var response = controller.getAll(2, 5, "match");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).containsExactly(item);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", true).containsEntry("hasPrev", true);
        verify(currencyService).getAllItems(2, 5, "match");
    }

    @Test
    void getByIdCreateAndUpdateReturnServiceResults() {
        CurrencyItem item = mock(CurrencyItem.class);
        CurrencyCreateDto createDto = mock(CurrencyCreateDto.class);
        CurrencyUpdateDto updateDto = mock(CurrencyUpdateDto.class);
        when(currencyService.getItemById(17L)).thenReturn(item);
        when(currencyService.create(createDto)).thenReturn(item);
        when(currencyService.update(17L, updateDto)).thenReturn(item);

        assertThat(controller.getById(17L).getBody().getData()).isSameAs(item);
        assertThat(controller.create(createDto).getBody().getData()).isSameAs(item);
        assertThat(controller.update(17L, updateDto).getBody().getData()).isSameAs(item);
        verify(currencyService).getItemById(17L);
        verify(currencyService).create(createDto);
        verify(currencyService).update(17L, updateDto);
    }

    @Test
    void deleteInvokesServiceAndReturnsLocalizedMessage() {
        when(i18n.msg("currency.deleted", 17L)).thenReturn("Deleted");
        var response = controller.delete(17L);

        verify(currencyService).softDelete(17L);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("currency.deleted");
    }
}

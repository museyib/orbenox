package com.orbenox.erp.domain.businesspartner;

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
class BusinessPartnerControllerTest {
    @Mock
    private BusinessPartnerService businessPartnerService;
    @Mock
    private LocalizationService i18n;
    @InjectMocks
    private BusinessPartnerController controller;

    @Test
    void getAllReturnsPageContentAndNavigationHeaders() {
        BusinessPartnerItem item = mock(BusinessPartnerItem.class);
        Slice<BusinessPartnerItem> slice = new SliceImpl<>(List.of(item), PageRequest.of(2, 5), true);
        when(businessPartnerService.getAllItems(2, 5, "match")).thenReturn(slice);
        var response = controller.getAll(2, 5, "match");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).containsExactly(item);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", true).containsEntry("hasPrev", true);
        verify(businessPartnerService).getAllItems(2, 5, "match");
    }

    @Test
    void getByIdCreateAndUpdateReturnServiceResults() {
        BusinessPartnerItem item = mock(BusinessPartnerItem.class);
        BusinessPartnerCreateDto createDto = mock(BusinessPartnerCreateDto.class);
        BusinessPartnerUpdateDto updateDto = mock(BusinessPartnerUpdateDto.class);
        when(businessPartnerService.getItemById(17L)).thenReturn(item);
        when(businessPartnerService.create(createDto)).thenReturn(item);
        when(businessPartnerService.update(17L, updateDto)).thenReturn(item);

        assertThat(controller.getById(17L).getBody().getData()).isSameAs(item);
        assertThat(controller.create(createDto).getBody().getData()).isSameAs(item);
        assertThat(controller.update(17L, updateDto).getBody().getData()).isSameAs(item);
        verify(businessPartnerService).getItemById(17L);
        verify(businessPartnerService).create(createDto);
        verify(businessPartnerService).update(17L, updateDto);
    }

    @Test
    void deleteInvokesServiceAndReturnsLocalizedMessage() {
        when(i18n.msg("businessPartner.deleted", 17L)).thenReturn("Deleted");
        var response = controller.delete(17L);

        verify(businessPartnerService).softDelete(17L);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("businessPartner.deleted");
    }
}

package com.orbenox.erp.domain.account;

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
class AccountControllerTest {
    @Mock
    private AccountService accountService;
    @Mock
    private LocalizationService i18n;
    @InjectMocks
    private AccountController controller;

    @Test
    void getAllReturnsPageContentAndNavigationHeaders() {
        AccountItem item = mock(AccountItem.class);
        Slice<AccountItem> slice = new SliceImpl<>(List.of(item), PageRequest.of(2, 5), true);
        when(accountService.getAllItems(2, 5, "match")).thenReturn(slice);
        var response = controller.getAll(2, 5, "match");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).containsExactly(item);
        assertThat(response.getBody().getHeaders()).containsEntry("hasNext", true).containsEntry("hasPrev", true);
        verify(accountService).getAllItems(2, 5, "match");
    }

    @Test
    void getByIdCreateAndUpdateReturnServiceResults() {
        AccountItem item = mock(AccountItem.class);
        AccountCreateDto createDto = mock(AccountCreateDto.class);
        AccountUpdateDto updateDto = mock(AccountUpdateDto.class);
        when(accountService.getItemById(17L)).thenReturn(item);
        when(accountService.create(createDto)).thenReturn(item);
        when(accountService.update(17L, updateDto)).thenReturn(item);

        assertThat(controller.getById(17L).getBody().getData()).isSameAs(item);
        assertThat(controller.create(createDto).getBody().getData()).isSameAs(item);
        assertThat(controller.update(17L, updateDto).getBody().getData()).isSameAs(item);
        verify(accountService).getItemById(17L);
        verify(accountService).create(createDto);
        verify(accountService).update(17L, updateDto);
    }

    @Test
    void deleteInvokesServiceAndReturnsLocalizedMessage() {
        when(i18n.msg("account.deleted", 17L)).thenReturn("Deleted");
        var response = controller.delete(17L);

        verify(accountService).softDelete(17L);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Deleted");
        assertThat(response.getBody().getMessageKey()).isEqualTo("account.deleted");
    }
}

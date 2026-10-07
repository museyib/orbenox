package com.orbenox.erp.domain.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    AccountRepository repository;
    @Mock
    AccountMapper mapper;
    @InjectMocks
    AccountService service;

    @Test
    void getsAllOrSearchedAccountsBasedOnSearchText() {
        Slice<AccountItem> all = mock(Slice.class);
        Slice<AccountItem> searched = mock(Slice.class);
        when(repository.getAllItems(PageRequest.of(0, 10))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(1, 5), "cash")).thenReturn(searched);

        assertThat(service.getAllItems(0, 10, null)).isSameAs(all);
        assertThat(service.getAllItems(1, 5, "cash")).isSameAs(searched);
        verify(repository).getAllItems(PageRequest.of(0, 10));
        verify(repository).getItemsSearched(PageRequest.of(1, 5), "cash");
    }

    @Test
    void delegatesItemLookup() {
        AccountItem item = mock(AccountItem.class);
        when(repository.getItemById(1L)).thenReturn(item);
        assertThat(service.getItemById(1L)).isSameAs(item);
    }

    @Test
    void createsAccountAndReturnsSavedProjection() {
        AccountCreateDto dto = mock(AccountCreateDto.class);
        Account entity = new Account();
        entity.setId(2L);
        AccountItem item = mock(AccountItem.class);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.getItemById(2L)).thenReturn(item);

        assertThat(service.create(dto)).isSameAs(item);
        verify(repository).save(entity);
        verify(repository).getItemById(2L);
    }

    @Test
    void updatesFoundAccountAndReturnsProjection() {
        AccountUpdateDto dto = mock(AccountUpdateDto.class);
        Account entity = new Account();
        AccountItem item = mock(AccountItem.class);
        when(repository.findByIdAndDeletedFalse(3L)).thenReturn(entity);
        when(repository.getItemById(3L)).thenReturn(item);

        assertThat(service.update(3L, dto)).isSameAs(item);
        verify(mapper).updateEntityFromDto(dto, entity);
        verify(repository).getItemById(3L);
    }

    @Test
    void softDeleteMarksAccountDeleted() {
        Account entity = new Account();
        when(repository.findByIdAndDeletedFalse(4L)).thenReturn(entity);
        service.softDelete(4L);
        assertThat(entity.isDeleted()).isTrue();
    }
}

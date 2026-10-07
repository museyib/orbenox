package com.orbenox.erp.domain.transactiontype;

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
class TransactionTypeServiceTest {
    @Mock
    TransactionTypeRepository repository;
    @Mock
    TransactionTypeMapper mapper;
    @InjectMocks
    TransactionTypeService service;

    @Test
    void getsAllOrSearchedTransactionTypes() {
        Slice<TransactionTypeItem> all = mock(Slice.class);
        Slice<TransactionTypeItem> searched = mock(Slice.class);
        when(repository.getAllItems(PageRequest.of(0, 10))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(1, 10), "sales")).thenReturn(searched);

        assertThat(service.getAllItems(0, 10, null)).isSameAs(all);
        assertThat(service.getAllItems(1, 10, "sales")).isSameAs(searched);
        verify(repository).getAllItems(PageRequest.of(0, 10));
        verify(repository).getItemsSearched(PageRequest.of(1, 10), "sales");
    }

    @Test
    void delegatesItemLookup() {
        TransactionTypeItem item = mock(TransactionTypeItem.class);
        when(repository.getItemById(13L)).thenReturn(item);
        assertThat(service.getItemById(13L)).isSameAs(item);
    }

    @Test
    void createsTransactionTypeAndReturnsProjection() {
        TransactionTypeCreateDto dto = mock(TransactionTypeCreateDto.class);
        TransactionType entity = new TransactionType();
        entity.setId(14L);
        TransactionTypeItem item = mock(TransactionTypeItem.class);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.getItemById(14L)).thenReturn(item);

        assertThat(service.create(dto)).isSameAs(item);
        verify(repository).save(entity);
        verify(repository).getItemById(14L);
    }

    @Test
    void updatesTransactionTypeAndReturnsProjection() {
        TransactionTypeUpdateDto dto = mock(TransactionTypeUpdateDto.class);
        TransactionType entity = new TransactionType();
        TransactionTypeItem item = mock(TransactionTypeItem.class);
        when(repository.findByIdAndDeletedFalse(15L)).thenReturn(entity);
        when(repository.getItemById(15L)).thenReturn(item);

        assertThat(service.update(15L, dto)).isSameAs(item);
        verify(mapper).updateEntityFromDto(dto, entity);
    }

    @Test
    void softDeleteMarksTransactionTypeDeleted() {
        TransactionType entity = new TransactionType();
        when(repository.findByIdAndDeletedFalse(16L)).thenReturn(entity);
        service.softDelete(16L);
        assertThat(entity.isDeleted()).isTrue();
    }
}

package com.orbenox.erp.domain.currency;

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
class CurrencyServiceTest {
    @Mock
    CurrencyRepository repository;
    @Mock
    CurrencyMapper mapper;
    @InjectMocks
    CurrencyService service;

    @Test
    void getsAllOrSearchedCurrencies() {
        Slice<CurrencyItem> all = mock(Slice.class);
        Slice<CurrencyItem> searched = mock(Slice.class);
        when(repository.getAllItems(PageRequest.of(0, 10))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(1, 10), "usd")).thenReturn(searched);

        assertThat(service.getAllItems(0, 10, " ")).isSameAs(all);
        assertThat(service.getAllItems(1, 10, "usd")).isSameAs(searched);
        verify(repository).getAllItems(PageRequest.of(0, 10));
        verify(repository).getItemsSearched(PageRequest.of(1, 10), "usd");
    }

    @Test
    void delegatesItemLookup() {
        CurrencyItem item = mock(CurrencyItem.class);
        when(repository.getItemById(17L)).thenReturn(item);
        assertThat(service.getItemById(17L)).isSameAs(item);
    }

    @Test
    void createsCurrencyAndReturnsProjection() {
        CurrencyCreateDto dto = mock(CurrencyCreateDto.class);
        Currency entity = new Currency();
        entity.setId(18L);
        CurrencyItem item = mock(CurrencyItem.class);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.getItemById(18L)).thenReturn(item);

        assertThat(service.create(dto)).isSameAs(item);
        verify(repository).save(entity);
    }

    @Test
    void updatesCurrencyPersistsMappedChanges() {
        CurrencyUpdateDto dto = mock(CurrencyUpdateDto.class);
        Currency entity = new Currency();
        CurrencyItem item = mock(CurrencyItem.class);
        when(repository.findByIdAndDeletedFalse(19L)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.getItemById(19L)).thenReturn(item);

        assertThat(service.update(19L, dto)).isSameAs(item);
        verify(mapper).updateEntityFromDto(dto, entity);
        verify(repository).save(entity);
        verify(repository).getItemById(19L);
    }

    @Test
    void softDeleteMarksCurrencyDeleted() {
        Currency entity = new Currency();
        when(repository.findByIdAndDeletedFalse(20L)).thenReturn(entity);
        service.softDelete(20L);
        assertThat(entity.isDeleted()).isTrue();
    }
}

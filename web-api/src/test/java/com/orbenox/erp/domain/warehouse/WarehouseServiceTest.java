package com.orbenox.erp.domain.warehouse;

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
class WarehouseServiceTest {
    @Mock
    WarehouseRepository repository;
    @Mock
    WarehouseMapper mapper;
    @InjectMocks
    WarehouseService service;

    @Test
    void getsAllOrSearchedWarehouses() {
        Slice<WarehouseItem> all = mock(Slice.class);
        Slice<WarehouseItem> searched = mock(Slice.class);
        when(repository.getAllItems(PageRequest.of(0, 10))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(1, 10), "main")).thenReturn(searched);

        assertThat(service.getAllItems(0, 10, "")).isSameAs(all);
        assertThat(service.getAllItems(1, 10, "main")).isSameAs(searched);
        verify(repository).getAllItems(PageRequest.of(0, 10));
        verify(repository).getItemsSearched(PageRequest.of(1, 10), "main");
    }

    @Test
    void delegatesItemLookup() {
        WarehouseItem item = mock(WarehouseItem.class);
        when(repository.getItemById(9L)).thenReturn(item);
        assertThat(service.getItemById(9L)).isSameAs(item);
    }

    @Test
    void createsWarehouseAndReturnsProjection() {
        WarehouseCreateDto dto = mock(WarehouseCreateDto.class);
        Warehouse entity = new Warehouse();
        entity.setId(10L);
        WarehouseItem item = mock(WarehouseItem.class);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.getItemById(10L)).thenReturn(item);

        assertThat(service.create(dto)).isSameAs(item);
        verify(repository).save(entity);
        verify(repository).getItemById(10L);
    }

    @Test
    void updatesWarehouseAndReturnsProjection() {
        WarehouseUpdateDto dto = mock(WarehouseUpdateDto.class);
        Warehouse entity = new Warehouse();
        WarehouseItem item = mock(WarehouseItem.class);
        when(repository.findByIdAndDeletedFalse(11L)).thenReturn(entity);
        when(repository.getItemById(11L)).thenReturn(item);

        assertThat(service.update(11L, dto)).isSameAs(item);
        verify(mapper).updateEntityFromDto(dto, entity);
    }

    @Test
    void softDeleteMarksWarehouseDeleted() {
        Warehouse entity = new Warehouse();
        when(repository.findByIdAndDeletedFalse(12L)).thenReturn(entity);
        service.softDelete(12L);
        assertThat(entity.isDeleted()).isTrue();
    }
}

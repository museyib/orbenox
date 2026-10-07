package com.orbenox.erp.domain.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitMapper mapper;
    @Mock
    private UnitRepository repository;
    @InjectMocks
    private UnitService service;

    @Test
    void getAllItemsSelectsSearchOrUnfilteredQuery() {
        Slice<UnitItem> all = mock(Slice.class);
        Slice<UnitItem> searched = mock(Slice.class);
        when(repository.getAllItems(PageRequest.of(2, 4))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(2, 4), "kg")).thenReturn(searched);

        assertThat(service.getAllItems(2, 4, null)).isSameAs(all);
        assertThat(service.getAllItems(2, 4, "kg")).isSameAs(searched);
        verify(repository).getAllItems(PageRequest.of(2, 4));
        verify(repository).getItemsSearched(PageRequest.of(2, 4), "kg");
    }

    @Test
    void findsUnitsForDimension() {
        List<UnitItem> units = List.of(mock(UnitItem.class));
        when(repository.getItemsByUnitDimensionId(8L)).thenReturn(units);

        assertThat(service.findAllByDimensionId(8L)).isSameAs(units);
    }

    @Test
    void getItemByIdDelegatesToRepository() {
        UnitItem item = mock(UnitItem.class);
        when(repository.getItemById(9L)).thenReturn(item);

        assertThat(service.getItemById(9L)).isSameAs(item);
    }

    @Test
    void createPersistsMappedUnitAndReturnsProjection() {
        UnitCreateDto dto = mock(UnitCreateDto.class);
        Unit unit = new Unit();
        unit.setId(10L);
        UnitItem item = mock(UnitItem.class);
        when(mapper.toEntity(dto)).thenReturn(unit);
        when(repository.save(unit)).thenReturn(unit);
        when(repository.getItemById(10L)).thenReturn(item);

        assertThat(service.create(dto)).isSameAs(item);
        verify(repository).save(unit);
        verify(repository).getItemById(10L);
    }

    @Test
    void updateMapsDtoAndReturnsProjection() {
        UnitUpdateDto dto = mock(UnitUpdateDto.class);
        Unit unit = new Unit();
        UnitItem item = mock(UnitItem.class);
        when(repository.findByIdAndDeletedFalse(11L)).thenReturn(unit);
        when(repository.getItemById(11L)).thenReturn(item);

        assertThat(service.update(11L, dto)).isSameAs(item);
        verify(mapper).updateEntityFromDto(dto, unit);
    }

    @Test
    void softDeleteMarksUnitDeleted() {
        Unit unit = new Unit();
        when(repository.findByIdAndDeletedFalse(12L)).thenReturn(unit);

        service.softDelete(12L);

        assertThat(unit.isDeleted()).isTrue();
    }
}

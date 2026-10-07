package com.orbenox.erp.domain.price;

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
class PriceListServiceTest {

    @Mock
    private PriceListRepository repository;
    @Mock
    private PriceListMapper mapper;
    @InjectMocks
    private PriceListService service;

    @Test
    void getAllItemsSelectsSearchOrUnfilteredQuery() {
        Slice<PriceListItem> all = mock(Slice.class);
        Slice<PriceListItem> searched = mock(Slice.class);
        when(repository.getAllItems(PageRequest.of(0, 10))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(0, 10), "retail")).thenReturn(searched);

        assertThat(service.getAllItems(0, 10, "")).isSameAs(all);
        assertThat(service.getAllItems(0, 10, "retail")).isSameAs(searched);
        verify(repository).getAllItems(PageRequest.of(0, 10));
        verify(repository).getItemsSearched(PageRequest.of(0, 10), "retail");
    }

    @Test
    void findAllExcludedRemovesSelectedListAndItsDescendants() {
        PriceListItem root = item(1L, null);
        PriceListItem child = item(2L, parent(1L));
        PriceListItem grandchild = item(3L, parent(2L));
        PriceListItem cycle = item(1L, parent(2L));
        PriceListItem unrelated = item(4L, null);
        when(repository.getAllItems()).thenReturn(List.of(root, child, grandchild, cycle, unrelated));
        List<PriceListItem.PriceListParent> allowed = List.of(mock(PriceListItem.PriceListParent.class));
        when(repository.getParentPriceListItems(List.of(4L))).thenReturn(allowed);

        assertThat(service.findAllExcluded(1L)).isSameAs(allowed);
        verify(repository).getParentPriceListItems(List.of(4L));
    }

    @Test
    void createSavesMappedEntityAndReturnsProjection() {
        PriceListCreateDto dto = mock(PriceListCreateDto.class);
        PriceList entity = new PriceList();
        entity.setId(21L);
        PriceListItem item = mock(PriceListItem.class);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.getItemById(21L)).thenReturn(item);

        assertThat(service.create(dto)).isSameAs(item);
        verify(repository).save(entity);
        verify(repository).getItemById(21L);
    }

    @Test
    void getItemByIdDelegatesToRepository() {
        PriceListItem item = mock(PriceListItem.class);
        when(repository.getItemById(20L)).thenReturn(item);

        assertThat(service.getItemById(20L)).isSameAs(item);
    }

    @Test
    void updateMapsChangesOntoFoundEntity() {
        PriceListUpdateDto dto = mock(PriceListUpdateDto.class);
        PriceList entity = new PriceList();
        PriceListItem item = mock(PriceListItem.class);
        when(repository.findByIdAndDeletedFalse(22L)).thenReturn(entity);
        when(repository.getItemById(22L)).thenReturn(item);

        assertThat(service.update(22L, dto)).isSameAs(item);
        verify(mapper).updateEntityFromDto(dto, entity);
        verify(repository).getItemById(22L);
    }

    @Test
    void softDeleteMarksFoundListDeleted() {
        PriceList entity = new PriceList();
        when(repository.findByIdAndDeletedFalse(23L)).thenReturn(entity);

        service.softDelete(23L);

        assertThat(entity.isDeleted()).isTrue();
        verify(repository).findByIdAndDeletedFalse(23L);
    }

    private PriceListItem item(Long id, PriceListItem.PriceListParent parent) {
        PriceListItem item = mock(PriceListItem.class);
        when(item.getId()).thenReturn(id);
        when(item.getParent()).thenReturn(parent);
        return item;
    }

    private PriceListItem.PriceListParent parent(Long id) {
        PriceListItem.PriceListParent parent = mock(PriceListItem.PriceListParent.class);
        when(parent.getId()).thenReturn(id);
        return parent;
    }
}

package com.orbenox.erp.domain.resource;

import com.orbenox.erp.enums.Action;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository repository;
    @Mock
    private ResourceMapper mapper;
    @InjectMocks
    private ResourceService service;

    @Test
    void getAllItems_selectsSearchOrUnfilteredRepositoryQuery() {
        Slice<ResourceItem> unfiltered = mock(Slice.class);
        Slice<ResourceItem> searched = mock(Slice.class);
        when(repository.getAllItems(PageRequest.of(1, 5))).thenReturn(unfiltered);
        when(repository.getItemsSearched(PageRequest.of(1, 5), "invoice")).thenReturn(searched);

        assertThat(service.getAllItems(1, 5, "  ")).isSameAs(unfiltered);
        assertThat(service.getAllItems(1, 5, "invoice")).isSameAs(searched);
        verify(repository).getAllItems(PageRequest.of(1, 5));
        verify(repository).getItemsSearched(PageRequest.of(1, 5), "invoice");
    }

    @Test
    void getItemByIdCombinesResourceAndActionItems() {
        ResourceItem item = mock(ResourceItem.class);
        when(repository.getItemById(7L)).thenReturn(item);
        when(repository.getActionItemsByResourceId(7L)).thenReturn(List.of("READ", "UPDATE"));

        ResourceData data = service.getItemById(7L);

        assertThat(data.getResource()).isSameAs(item);
        assertThat(data.getActions()).containsExactly("READ", "UPDATE");
    }

    @Test
    void createMapsActionNamesAndReturnsSavedItem() {
        ResourceCreateDto dto = new ResourceCreateDto(true, "INVOICE", "Invoice", Set.of("READ", "CREATE"));
        Resource entity = new Resource();
        entity.setId(11L);
        ResourceItem item = mock(ResourceItem.class);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.getItemById(11L)).thenReturn(item);

        assertThat(service.create(dto)).isSameAs(item);

        assertThat(entity.getActions()).containsExactlyInAnyOrder(Action.READ, Action.CREATE);
        verify(repository).save(entity);
        verify(repository).getItemById(11L);
    }

    @Test
    void updateReplacesActionsAndReturnsUpdatedProjection() {
        ResourceUpdateDto dto = new ResourceUpdateDto(12L, true, "INVOICE", "Invoice", Set.of("APPROVE"));
        Resource entity = new Resource();
        entity.setActions(Set.of(Action.READ));
        ResourceItem item = mock(ResourceItem.class);
        when(repository.findByIdAndDeletedFalse(12L)).thenReturn(entity);
        when(repository.getItemById(12L)).thenReturn(item);

        assertThat(service.update(12L, dto)).isSameAs(item);

        verify(mapper).updateEntityFromDto(dto, entity);
        assertThat(entity.getActions()).containsExactly(Action.APPROVE);
    }

    @Test
    void softDeleteMarksFoundResourceDeleted() {
        Resource entity = new Resource();
        when(repository.findByIdAndDeletedFalse(13L)).thenReturn(entity);

        service.softDelete(13L);

        assertThat(entity.isDeleted()).isTrue();
        verify(repository).findByIdAndDeletedFalse(13L);
    }
}

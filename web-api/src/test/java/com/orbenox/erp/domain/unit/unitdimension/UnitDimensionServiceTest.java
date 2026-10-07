package com.orbenox.erp.domain.unit.unitdimension;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UnitDimensionServiceTest {

    private final UnitDimensionRepository repository = mock(UnitDimensionRepository.class);
    private final UnitDimensionMapper mapper = mock(UnitDimensionMapper.class);
    private final UnitDimensionService service = new UnitDimensionService(repository, mapper);

    @Test
    void getAllItems_shouldUseSearchRepositoryForNonBlankSearch() {
        Slice<UnitDimensionItem> items = mock(Slice.class);
        when(repository.getItemsSearched(PageRequest.of(1, 5), "length")).thenReturn(items);

        assertThat(service.getAllItems(1, 5, "length")).isSameAs(items);
    }

    @Test
    void update_shouldMapOntoExistingDimensionAndReturnItem() {
        UnitDimension entity = new UnitDimension();
        UnitDimensionItem item = mock(UnitDimensionItem.class);
        UnitDimensionDto dto = new UnitDimensionDto(4L, true, "LEN", "Length");
        when(repository.findByIdAndDeletedFalse(4L)).thenReturn(entity);
        when(repository.getItemById(4L)).thenReturn(item);

        assertThat(service.update(4L, dto)).isSameAs(item);
        verify(mapper).updateEntityFromDto(dto, entity);
    }

    @Test
    void softDelete_shouldMarkDimensionDeleted() {
        UnitDimension entity = new UnitDimension();
        when(repository.findByIdAndDeletedFalse(4L)).thenReturn(entity);

        service.softDelete(4L);

        assertThat(entity.isDeleted()).isTrue();
    }
}

package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.BrandCreateDto;
import com.orbenox.erp.domain.product.dto.BrandUpdateDto;
import com.orbenox.erp.domain.product.entity.Brand;
import com.orbenox.erp.domain.product.mapper.BrandMapper;
import com.orbenox.erp.domain.product.projection.BrandItem;
import com.orbenox.erp.domain.product.repository.BrandRepository;
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
class BrandServiceTest {
    @Mock
    BrandRepository repository;
    @Mock
    BrandMapper mapper;
    @InjectMocks
    BrandService service;

    @Test
    void choosesUnfilteredOrSearchedPageAndDelegatesLookup() {
        Slice<BrandItem> all = mock(Slice.class);
        Slice<BrandItem> searched = mock(Slice.class);
        BrandItem item = mock(BrandItem.class);
        when(repository.getAllItems(PageRequest.of(0, 8))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(1, 4), "acme")).thenReturn(searched);
        when(repository.getItemById(1L)).thenReturn(item);

        assertThat(service.getAllItems(0, 8, "")).isSameAs(all);
        assertThat(service.getAllItems(1, 4, "acme")).isSameAs(searched);
        assertThat(service.getItemById(1L)).isSameAs(item);
        verify(repository).getAllItems(PageRequest.of(0, 8));
        verify(repository).getItemsSearched(PageRequest.of(1, 4), "acme");
    }

    @Test
    void createsAndUpdatesBrandUsingMapperAndRepository() {
        BrandCreateDto createDto = mock(BrandCreateDto.class);
        BrandUpdateDto updateDto = mock(BrandUpdateDto.class);
        Brand created = new Brand();
        created.setId(2L);
        Brand updated = new Brand();
        BrandItem createItem = mock(BrandItem.class);
        BrandItem updateItem = mock(BrandItem.class);
        when(mapper.toEntity(createDto)).thenReturn(created);
        when(repository.save(created)).thenReturn(created);
        when(repository.getItemById(2L)).thenReturn(createItem);
        when(repository.findByIdAndDeletedFalse(3L)).thenReturn(updated);
        when(repository.getItemById(3L)).thenReturn(updateItem);

        assertThat(service.create(createDto)).isSameAs(createItem);
        assertThat(service.update(3L, updateDto)).isSameAs(updateItem);
        verify(repository).save(created);
        verify(mapper).updateEntityFromDto(updateDto, updated);
    }

    @Test
    void softDeleteMarksActiveBrandDeleted() {
        Brand entity = new Brand();
        when(repository.findByIdAndDeletedFalse(4L)).thenReturn(entity);
        service.softDelete(4L);
        assertThat(entity.isDeleted()).isTrue();
    }
}

package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductClassCreateDto;
import com.orbenox.erp.domain.product.dto.ProductClassUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductClass;
import com.orbenox.erp.domain.product.mapper.ProductClassMapper;
import com.orbenox.erp.domain.product.projection.ProductClassItem;
import com.orbenox.erp.domain.product.repository.ProductClassRepository;
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
class ProductClassServiceTest {
    @Mock
    ProductClassRepository repository;
    @Mock
    ProductClassMapper mapper;
    @InjectMocks
    ProductClassService service;

    @Test
    void choosesUnfilteredOrSearchedPageAndDelegatesLookup() {
        Slice<ProductClassItem> all = mock(Slice.class);
        Slice<ProductClassItem> searched = mock(Slice.class);
        ProductClassItem item = mock(ProductClassItem.class);
        when(repository.getAllItems(PageRequest.of(0, 8))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(1, 4), "stock")).thenReturn(searched);
        when(repository.getItemById(1L)).thenReturn(item);

        assertThat(service.getAllItems(0, 8, "")).isSameAs(all);
        assertThat(service.getAllItems(1, 4, "stock")).isSameAs(searched);
        assertThat(service.getItemById(1L)).isSameAs(item);
        verify(repository).getAllItems(PageRequest.of(0, 8));
        verify(repository).getItemsSearched(PageRequest.of(1, 4), "stock");
    }

    @Test
    void createsAndUpdatesClassUsingMapperAndRepository() {
        ProductClassCreateDto createDto = mock(ProductClassCreateDto.class);
        ProductClassUpdateDto updateDto = mock(ProductClassUpdateDto.class);
        ProductClass created = new ProductClass();
        created.setId(2L);
        ProductClass updated = new ProductClass();
        ProductClassItem createItem = mock(ProductClassItem.class);
        ProductClassItem updateItem = mock(ProductClassItem.class);
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
    void softDeleteMarksActiveClassDeleted() {
        ProductClass entity = new ProductClass();
        when(repository.findByIdAndDeletedFalse(4L)).thenReturn(entity);
        service.softDelete(4L);
        assertThat(entity.isDeleted()).isTrue();
    }
}

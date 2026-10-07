package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductTypeCreateDto;
import com.orbenox.erp.domain.product.dto.ProductTypeUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductType;
import com.orbenox.erp.domain.product.mapper.ProductTypeMapper;
import com.orbenox.erp.domain.product.projection.ProductTypeItem;
import com.orbenox.erp.domain.product.repository.ProductTypeRepository;
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
class ProductTypeServiceTest {
    @Mock
    ProductTypeRepository repository;
    @Mock
    ProductTypeMapper mapper;
    @InjectMocks
    ProductTypeService service;

    @Test
    void choosesUnfilteredOrSearchedPageAndDelegatesLookup() {
        Slice<ProductTypeItem> all = mock(Slice.class);
        Slice<ProductTypeItem> searched = mock(Slice.class);
        ProductTypeItem item = mock(ProductTypeItem.class);
        when(repository.getAllItems(PageRequest.of(0, 8))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(1, 4), "service")).thenReturn(searched);
        when(repository.getItemById(1L)).thenReturn(item);

        assertThat(service.getAllItems(0, 8, null)).isSameAs(all);
        assertThat(service.getAllItems(1, 4, "service")).isSameAs(searched);
        assertThat(service.getItemById(1L)).isSameAs(item);
        verify(repository).getAllItems(PageRequest.of(0, 8));
        verify(repository).getItemsSearched(PageRequest.of(1, 4), "service");
    }

    @Test
    void createsAndUpdatesTypeUsingMapperAndRepository() {
        ProductTypeCreateDto createDto = mock(ProductTypeCreateDto.class);
        ProductTypeUpdateDto updateDto = mock(ProductTypeUpdateDto.class);
        ProductType created = new ProductType();
        created.setId(2L);
        ProductType updated = new ProductType();
        ProductTypeItem createItem = mock(ProductTypeItem.class);
        ProductTypeItem updateItem = mock(ProductTypeItem.class);
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
    void softDeleteMarksActiveTypeDeleted() {
        ProductType entity = new ProductType();
        when(repository.findByIdAndDeletedFalse(4L)).thenReturn(entity);
        service.softDelete(4L);
        assertThat(entity.isDeleted()).isTrue();
    }
}

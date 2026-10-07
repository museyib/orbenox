package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductCategoryCreateDto;
import com.orbenox.erp.domain.product.dto.ProductCategoryUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductCategory;
import com.orbenox.erp.domain.product.mapper.ProductCategoryMapper;
import com.orbenox.erp.domain.product.projection.ProductCategoryItem;
import com.orbenox.erp.domain.product.repository.ProductCategoryRepository;
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
class ProductCategoryServiceTest {
    @Mock
    ProductCategoryRepository repository;
    @Mock
    ProductCategoryMapper mapper;
    @InjectMocks
    ProductCategoryService service;

    @Test
    void choosesUnfilteredOrSearchedPageAndDelegatesLookup() {
        Slice<ProductCategoryItem> all = mock(Slice.class);
        Slice<ProductCategoryItem> searched = mock(Slice.class);
        ProductCategoryItem item = mock(ProductCategoryItem.class);
        when(repository.getAllItems(PageRequest.of(0, 8))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(1, 4), "food")).thenReturn(searched);
        when(repository.getItemById(1L)).thenReturn(item);

        assertThat(service.getAllItems(0, 8, " ")).isSameAs(all);
        assertThat(service.getAllItems(1, 4, "food")).isSameAs(searched);
        assertThat(service.getItemById(1L)).isSameAs(item);
        verify(repository).getAllItems(PageRequest.of(0, 8));
        verify(repository).getItemsSearched(PageRequest.of(1, 4), "food");
    }

    @Test
    void createsAndUpdatesCategoryUsingMapperAndRepository() {
        ProductCategoryCreateDto createDto = mock(ProductCategoryCreateDto.class);
        ProductCategoryUpdateDto updateDto = mock(ProductCategoryUpdateDto.class);
        ProductCategory created = new ProductCategory();
        created.setId(2L);
        ProductCategory updated = new ProductCategory();
        ProductCategoryItem createItem = mock(ProductCategoryItem.class);
        ProductCategoryItem updateItem = mock(ProductCategoryItem.class);
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
    void softDeleteMarksActiveCategoryDeleted() {
        ProductCategory entity = new ProductCategory();
        when(repository.findByIdAndDeletedFalse(4L)).thenReturn(entity);
        service.softDelete(4L);
        assertThat(entity.isDeleted()).isTrue();
    }
}

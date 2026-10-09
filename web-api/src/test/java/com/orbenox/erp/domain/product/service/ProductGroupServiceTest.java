package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductGroupCreateDto;
import com.orbenox.erp.domain.product.dto.ProductGroupUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductGroup;
import com.orbenox.erp.domain.product.mapper.ProductGroupMapper;
import com.orbenox.erp.domain.product.projection.ProductGroupItem;
import com.orbenox.erp.domain.product.projection.SimpleProductGroupItem;
import com.orbenox.erp.domain.product.repository.ProductGroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductGroupServiceTest {
    @Mock
    ProductGroupRepository repository;
    @Mock
    ProductGroupMapper mapper;
    @InjectMocks
    ProductGroupService service;

    @Test
    void choosesUnfilteredOrSearchedPageAndDelegatesLookup() {
        Slice<ProductGroupItem> all = mock(Slice.class);
        Slice<ProductGroupItem> searched = mock(Slice.class);
        ProductGroupItem item = mock(ProductGroupItem.class);
        when(repository.getAllItems(PageRequest.of(0, 8))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(1, 4), "tools")).thenReturn(searched);
        when(repository.getItemById(1L)).thenReturn(item);

        assertThat(service.getAllItems(0, 8, "")).isSameAs(all);
        assertThat(service.getAllItems(1, 4, "tools")).isSameAs(searched);
        assertThat(service.getItemById(1L)).isSameAs(item);
    }

    @Test
    void findAllExcludedRemovesSelectedGroupAndNestedDescendants() {
        ProductGroupItem root = group(1L, null);
        ProductGroupItem child = group(2L, parent(1L));
        ProductGroupItem descendant = group(3L, parent(2L));
        ProductGroupItem outside = group(4L, null);
        List<ProductGroupItem> groups = new ArrayList<>(List.of(root, child, descendant, outside));
        List<Long> allowedIds = List.of(4L);
        List<SimpleProductGroupItem> allowed = List.of(mock(SimpleProductGroupItem.class));
        when(repository.getAllItems()).thenReturn(groups);
        when(repository.getItemsExcluded(allowedIds)).thenReturn(allowed);

        assertThat(service.findAllExcluded(1L)).isSameAs(allowed);
        verify(repository).getItemsExcluded(allowedIds);
    }

    @Test
    void createAndUpdateSaveAndMapGroupData() {
        ProductGroupCreateDto createDto = mock(ProductGroupCreateDto.class);
        ProductGroupUpdateDto updateDto = mock(ProductGroupUpdateDto.class);
        ProductGroup created = new ProductGroup();
        created.setId(5L);
        ProductGroup existing = new ProductGroup();
        ProductGroupItem createdItem = mock(ProductGroupItem.class);
        ProductGroupItem updatedItem = mock(ProductGroupItem.class);
        when(mapper.toEntity(createDto)).thenReturn(created);
        when(repository.save(created)).thenReturn(created);
        when(repository.getItemById(5L)).thenReturn(createdItem);
        when(repository.findByIdAndDeletedFalse(6L)).thenReturn(existing);
        when(repository.getItemById(6L)).thenReturn(updatedItem);

        assertThat(service.create(createDto)).isSameAs(createdItem);
        assertThat(service.update(6L, updateDto)).isSameAs(updatedItem);
        verify(mapper).updateEntityFromDto(updateDto, existing);
    }

    @Test
    void softDeleteMarksGroupDeleted() {
        ProductGroup entity = new ProductGroup();
        when(repository.findByIdAndDeletedFalse(7L)).thenReturn(entity);
        service.softDelete(7L);
        assertThat(entity.isDeleted()).isTrue();
    }

    private ProductGroupItem group(Long id, SimpleProductGroupItem parent) {
        ProductGroupItem group = mock(ProductGroupItem.class);
        when(group.getId()).thenReturn(id);
        when(group.getParent()).thenReturn(parent);
        return group;
    }

    private SimpleProductGroupItem parent(Long id) {
        SimpleProductGroupItem parent = mock(SimpleProductGroupItem.class);
        when(parent.getId()).thenReturn(id);
        return parent;
    }
}

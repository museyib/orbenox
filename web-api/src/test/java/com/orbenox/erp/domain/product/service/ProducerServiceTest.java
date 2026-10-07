package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProducerCreateDto;
import com.orbenox.erp.domain.product.dto.ProducerUpdateDto;
import com.orbenox.erp.domain.product.entity.Producer;
import com.orbenox.erp.domain.product.mapper.ProducerMapper;
import com.orbenox.erp.domain.product.projection.ProducerItem;
import com.orbenox.erp.domain.product.repository.ProducerRepository;
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
class ProducerServiceTest {
    @Mock
    ProducerRepository repository;
    @Mock
    ProducerMapper mapper;
    @InjectMocks
    ProducerService service;

    @Test
    void choosesUnfilteredOrSearchedPageAndDelegatesLookup() {
        Slice<ProducerItem> all = mock(Slice.class);
        Slice<ProducerItem> searched = mock(Slice.class);
        ProducerItem item = mock(ProducerItem.class);
        when(repository.getAllItems(PageRequest.of(0, 8))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(1, 4), "north")).thenReturn(searched);
        when(repository.getItemById(1L)).thenReturn(item);

        assertThat(service.getAllItems(0, 8, null)).isSameAs(all);
        assertThat(service.getAllItems(1, 4, "north")).isSameAs(searched);
        assertThat(service.getItemById(1L)).isSameAs(item);
        verify(repository).getAllItems(PageRequest.of(0, 8));
        verify(repository).getItemsSearched(PageRequest.of(1, 4), "north");
    }

    @Test
    void createsAndUpdatesProducerUsingMapperAndRepository() {
        ProducerCreateDto createDto = mock(ProducerCreateDto.class);
        ProducerUpdateDto updateDto = mock(ProducerUpdateDto.class);
        Producer created = new Producer();
        created.setId(2L);
        Producer updated = new Producer();
        ProducerItem createItem = mock(ProducerItem.class);
        ProducerItem updateItem = mock(ProducerItem.class);
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
    void softDeleteMarksActiveProducerDeleted() {
        Producer entity = new Producer();
        when(repository.findByIdAndDeletedFalse(4L)).thenReturn(entity);
        service.softDelete(4L);
        assertThat(entity.isDeleted()).isTrue();
    }
}

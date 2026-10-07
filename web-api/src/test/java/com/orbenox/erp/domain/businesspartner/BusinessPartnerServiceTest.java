package com.orbenox.erp.domain.businesspartner;

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
class BusinessPartnerServiceTest {
    @Mock
    BusinessPartnerRepository repository;
    @Mock
    BusinessPartnerMapper mapper;
    @InjectMocks
    BusinessPartnerService service;

    @Test
    void getsAllOrSearchedBusinessPartners() {
        Slice<BusinessPartnerItem> all = mock(Slice.class);
        Slice<BusinessPartnerItem> searched = mock(Slice.class);
        when(repository.getAllItems(PageRequest.of(0, 20))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(2, 10), "acme")).thenReturn(searched);

        assertThat(service.getAllItems(0, 20, " ")).isSameAs(all);
        assertThat(service.getAllItems(2, 10, "acme")).isSameAs(searched);
        verify(repository).getAllItems(PageRequest.of(0, 20));
        verify(repository).getItemsSearched(PageRequest.of(2, 10), "acme");
    }

    @Test
    void delegatesItemLookup() {
        BusinessPartnerItem item = mock(BusinessPartnerItem.class);
        when(repository.getItemById(5L)).thenReturn(item);
        assertThat(service.getItemById(5L)).isSameAs(item);
    }

    @Test
    void createsPartnerAndReturnsProjection() {
        BusinessPartnerCreateDto dto = mock(BusinessPartnerCreateDto.class);
        BusinessPartner entity = new BusinessPartner();
        entity.setId(6L);
        BusinessPartnerItem item = mock(BusinessPartnerItem.class);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.getItemById(6L)).thenReturn(item);

        assertThat(service.create(dto)).isSameAs(item);
        verify(repository).save(entity);
        verify(repository).getItemById(6L);
    }

    @Test
    void updatesPartnerAndReturnsProjection() {
        BusinessPartnerUpdateDto dto = mock(BusinessPartnerUpdateDto.class);
        BusinessPartner entity = new BusinessPartner();
        BusinessPartnerItem item = mock(BusinessPartnerItem.class);
        when(repository.findByIdAndDeletedFalse(7L)).thenReturn(entity);
        when(repository.getItemById(7L)).thenReturn(item);

        assertThat(service.update(7L, dto)).isSameAs(item);
        verify(mapper).updateEntityFromDto(dto, entity);
    }

    @Test
    void softDeleteMarksPartnerDeleted() {
        BusinessPartner entity = new BusinessPartner();
        when(repository.findByIdAndDeletedFalse(8L)).thenReturn(entity);
        service.softDelete(8L);
        assertThat(entity.isDeleted()).isTrue();
    }
}

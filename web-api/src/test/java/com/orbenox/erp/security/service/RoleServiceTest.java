package com.orbenox.erp.security.service;

import com.orbenox.erp.security.dto.RoleCreateDto;
import com.orbenox.erp.security.dto.RoleUpdateDto;
import com.orbenox.erp.security.entity.AppRole;
import com.orbenox.erp.security.mapper.RoleMapper;
import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RoleServiceTest {
    private RoleRepository repository;
    private RoleMapper mapper;
    private RoleService service;

    @BeforeEach
    void setUp() {
        repository = mock(RoleRepository.class);
        mapper = mock(RoleMapper.class);
        service = new RoleService(repository, mapper);
    }

    @Test
    void getAllItems_shouldChoosePagedOrSearchQuery() {
        Slice<RoleItem> all = mock(Slice.class);
        Slice<RoleItem> matches = mock(Slice.class);
        when(repository.getAllItems(PageRequest.of(0, 20))).thenReturn(all);
        when(repository.getItemsSearched(PageRequest.of(0, 20), "finance")).thenReturn(matches);

        assertThat(service.getAllItems(0, 20, null)).isSameAs(all);
        assertThat(service.getAllItems(0, 20, "finance")).isSameAs(matches);
    }

    @Test
    void getItemById_shouldReturnRepositoryProjection() {
        RoleItem role = mock(RoleItem.class);
        when(repository.getItemById(2L)).thenReturn(role);

        assertThat(service.getItemById(2L)).isSameAs(role);
    }

    @Test
    void createAndUpdate_shouldMapSaveAndReloadRole() {
        RoleCreateDto createDto = new RoleCreateDto(true, "FINANCE", "Finance");
        AppRole newEntity = new AppRole();
        newEntity.setId(3L);
        AppRole saved = new AppRole();
        saved.setId(3L);
        RoleItem created = mock(RoleItem.class);
        when(mapper.toEntity(createDto)).thenReturn(newEntity);
        when(repository.save(newEntity)).thenReturn(saved);
        when(repository.getItemById(3L)).thenReturn(created);

        assertThat(service.create(createDto)).isSameAs(created);

        RoleUpdateDto updateDto = new RoleUpdateDto(3L, true, "FINANCE", "Finance");
        AppRole existing = new AppRole();
        RoleItem updated = mock(RoleItem.class);
        when(repository.findByIdAndDeletedFalse(3L)).thenReturn(existing);
        when(repository.getItemById(3L)).thenReturn(updated);
        assertThat(service.update(3L, updateDto)).isSameAs(updated);
        verify(mapper).updateEntityFromDTO(updateDto, existing);
    }

    @Test
    void softDelete_shouldMarkRoleDeleted() {
        AppRole role = new AppRole();
        when(repository.findByIdAndDeletedFalse(3L)).thenReturn(role);

        service.softDelete(3L);

        assertThat(role.isDeleted()).isTrue();
    }
}

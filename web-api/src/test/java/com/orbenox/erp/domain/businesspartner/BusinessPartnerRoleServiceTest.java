package com.orbenox.erp.domain.businesspartner;

import com.orbenox.erp.enums.PartnerRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BusinessPartnerRoleServiceTest {

    private final BusinessPartnerRoleRepository roleRepository = mock(BusinessPartnerRoleRepository.class);
    private final BusinessPartnerRoleMapper roleMapper = mock(BusinessPartnerRoleMapper.class);
    private final BusinessPartnerRepository partnerRepository = mock(BusinessPartnerRepository.class);
    private final BusinessPartnerRoleService service =
            new BusinessPartnerRoleService(roleRepository, roleMapper, partnerRepository);

    @Test
    void create_shouldAssignPartnerAndReturnSavedItem() {
        BusinessPartnerRoleCreateDto dto = new BusinessPartnerRoleCreateDto(true, 7L, PartnerRole.CUSTOMER);
        BusinessPartnerRole entity = new BusinessPartnerRole();
        BusinessPartner partner = new BusinessPartner();
        BusinessPartnerRoleItem item = mock(BusinessPartnerRoleItem.class);
        when(roleMapper.toEntity(dto)).thenReturn(entity);
        when(partnerRepository.getReferenceById(7L)).thenReturn(partner);
        when(roleRepository.save(entity)).thenAnswer(invocation -> {
            entity.setId(9L);
            return entity;
        });
        when(roleRepository.getItemById(9L)).thenReturn(item);

        assertThat(service.create(dto)).isSameAs(item);
        assertThat(entity.getPartner()).isSameAs(partner);
    }

    @Test
    void update_shouldMapFieldsAndReplacePartner() {
        BusinessPartnerRoleUpdateDto dto = new BusinessPartnerRoleUpdateDto(9L, true, 7L, PartnerRole.CUSTOMER);
        BusinessPartnerRole entity = new BusinessPartnerRole();
        BusinessPartner partner = new BusinessPartner();
        BusinessPartnerRoleItem item = mock(BusinessPartnerRoleItem.class);
        when(roleRepository.findByIdAndDeletedFalse(9L)).thenReturn(entity);
        when(partnerRepository.getReferenceById(7L)).thenReturn(partner);
        when(roleRepository.getItemById(9L)).thenReturn(item);

        assertThat(service.update(9L, dto)).isSameAs(item);
        verify(roleMapper).updateEntityFromDto(dto, entity);
        assertThat(entity.getPartner()).isSameAs(partner);
    }

    @Test
    void softDelete_shouldMarkRoleDeleted() {
        BusinessPartnerRole entity = new BusinessPartnerRole();
        when(roleRepository.findByIdAndDeletedFalse(9L)).thenReturn(entity);

        service.softDelete(9L);

        assertThat(entity.isDeleted()).isTrue();
    }
}

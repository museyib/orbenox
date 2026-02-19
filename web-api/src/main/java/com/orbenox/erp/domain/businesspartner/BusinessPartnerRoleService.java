package com.orbenox.erp.domain.businesspartner;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


import static com.orbenox.erp.config.CacheConfig.CacheNames.*;
import static com.orbenox.erp.common.Utilities.isBlank;

@Service
@RequiredArgsConstructor
public class BusinessPartnerRoleService {
    private final BusinessPartnerRoleRepository businessPartnerRoleRepository;
    private final BusinessPartnerRoleMapper businessPartnerRoleMapper;
    private final BusinessPartnerRepository businessPartnerRepository;

    @Cacheable(BUSINESS_PARTNER_ROLES)
    public Slice<BusinessPartnerRoleItem> getAllItems(int page, int size, String search) {
        if (isBlank(search))
            return businessPartnerRoleRepository.getAllItems(PageRequest.of(page, size));
        return businessPartnerRoleRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public BusinessPartnerRoleItem getItemById(Long id) {
        return businessPartnerRoleRepository.getItemById(id);
    }

    @CacheEvict(value = {BUSINESS_PARTNER_ROLES, LOOKUPS}, allEntries = true)
    public BusinessPartnerRoleItem create(BusinessPartnerRoleCreateDto dto) {
        BusinessPartnerRole entity = businessPartnerRoleMapper.toEntity(dto);
        entity.setPartner(businessPartnerRepository.getReferenceById(dto.partnerId()));
        entity = businessPartnerRoleRepository.save(entity);
        return businessPartnerRoleRepository.getItemById(entity.getId());
    }

    @CacheEvict(value = {BUSINESS_PARTNER_ROLES, LOOKUPS}, allEntries = true)
    @Transactional
    public BusinessPartnerRoleItem update(Long id, BusinessPartnerRoleUpdateDto dto) {
        BusinessPartnerRole entity = businessPartnerRoleRepository.findByIdAndDeletedFalse(id);
        businessPartnerRoleMapper.updateEntityFromDto(dto, entity);
        entity.setPartner(businessPartnerRepository.getReferenceById(dto.partnerId()));
        return businessPartnerRoleRepository.getItemById(id);
    }

    @CacheEvict(value = {BUSINESS_PARTNER_ROLES, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        BusinessPartnerRole entity = businessPartnerRoleRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}




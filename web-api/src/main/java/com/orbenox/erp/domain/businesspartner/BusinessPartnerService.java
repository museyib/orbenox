package com.orbenox.erp.domain.businesspartner;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


import static com.orbenox.erp.config.CacheConfig.CacheNames.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class BusinessPartnerService {
    private final BusinessPartnerRepository businessPartnerRepository;
    private final BusinessPartnerMapper businessPartnerMapper;

    @Cacheable(BUSINESS_PARTNERS)
    public Slice<BusinessPartnerItem> getAllItems(int page, int size, String search) {
        if (isEmpty(search))
            return businessPartnerRepository.getAllItems(PageRequest.of(page, size));
        return businessPartnerRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public BusinessPartnerItem getItemById(Long id) {
        return businessPartnerRepository.getItemById(id);
    }

    @CacheEvict(value = {BUSINESS_PARTNERS, LOOKUPS}, allEntries = true)
    public BusinessPartnerItem create(BusinessPartnerCreateDto dto) {
        BusinessPartner entity = businessPartnerRepository.save(businessPartnerMapper.toEntity(dto));
        return businessPartnerRepository.getItemById(entity.getId());
    }

    @CacheEvict(value = {BUSINESS_PARTNERS, LOOKUPS}, allEntries = true)
    @Transactional
    public BusinessPartnerItem update(Long id, BusinessPartnerUpdateDto dto) {
        BusinessPartner entity = businessPartnerRepository.findByIdAndDeletedFalse(id);
        businessPartnerMapper.updateEntityFromDto(dto, entity);
        return businessPartnerRepository.getItemById(id);
    }

    @CacheEvict(value = {BUSINESS_PARTNERS, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        BusinessPartner entity = businessPartnerRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}




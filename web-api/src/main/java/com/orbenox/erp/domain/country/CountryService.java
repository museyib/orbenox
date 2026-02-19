package com.orbenox.erp.domain.country;

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
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Cacheable(COUNTRIES)
    public Slice<CountryItem> getAllItems(int page, int size, String search) {
        if (isBlank(search))
            return countryRepository.getAllItems(PageRequest.of(page, size));
        return countryRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public CountryItem getItemById(Long id) {
        return countryRepository.getItemById(id);
    }

    @CacheEvict(value = {COUNTRIES, LOOKUPS}, allEntries = true)
    public CountryItem create(CountryCreateDto dto) {
        Country country = countryRepository.save(countryMapper.toEntity(dto));
        return countryRepository.getItemById(country.getId());
    }

    @CacheEvict(value = {COUNTRIES, LOOKUPS}, allEntries = true)
    @Transactional
    public CountryItem update(Long id, CountryUpdateDto dto) {
        Country country = countryRepository.findByIdAndDeletedFalse(id);
        countryMapper.updateEntityFromDto(dto, country);
        return countryRepository.getItemById(id);
    }

    @CacheEvict(value = {COUNTRIES, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Country entity = countryRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}





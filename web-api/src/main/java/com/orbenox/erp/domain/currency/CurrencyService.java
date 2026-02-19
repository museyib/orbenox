package com.orbenox.erp.domain.currency;

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
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Cacheable(CURRENCIES)
    public Slice<CurrencyItem> getAllItems(int page, int size, String search) {
        if (isBlank(search))
            return currencyRepository.getAllItems(PageRequest.of(page, size));
        return currencyRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public CurrencyItem getItemById(Long id) {
        return currencyRepository.getItemById(id);
    }

    @CacheEvict(value = {CURRENCIES, LOOKUPS}, allEntries = true)
    public CurrencyItem create(CurrencyCreateDto dto) {
        Currency currency = currencyRepository.save(currencyMapper.toEntity(dto));
        return currencyRepository.getItemById(currency.getId());
    }

    @CacheEvict(value = {CURRENCIES, LOOKUPS}, allEntries = true)
    @Transactional
    public CurrencyItem update(Long id, CurrencyUpdateDto dto) {
        Currency entity = currencyRepository.findByIdAndDeletedFalse(id);
        currencyMapper.updateEntityFromDto(dto, entity);
        currencyRepository.save(entity);
        return currencyRepository.getItemById(id);
    }

    @CacheEvict(value = {CURRENCIES, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Currency entity = currencyRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}





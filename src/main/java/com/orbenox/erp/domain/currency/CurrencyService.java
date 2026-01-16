package com.orbenox.erp.domain.currency;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    public List<CurrencyItem> getAllItems() {
        return currencyRepository.getAllItems();
    }

    public CurrencyItem getItemById(Long id) {
        return currencyRepository.getItemById(id);
    }

    @CacheEvict(value = "lookups", allEntries = true)
    public CurrencyItem create(CurrencyDto dto) {
        Currency currency = currencyRepository.save(currencyMapper.toEntity(dto));
        return currencyRepository.getItemById(currency.getId());
    }

    @CacheEvict(value = "lookups", key = "#id")
    @Transactional
    public CurrencyItem update(Long id, CurrencyDto dto) {
        Currency entity = currencyRepository.findByIdAndDeletedFalse(id);
        currencyMapper.updateEntityFromDto(dto, entity);
        return currencyRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Currency entity = currencyRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}

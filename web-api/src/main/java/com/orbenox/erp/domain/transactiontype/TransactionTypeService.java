package com.orbenox.erp.domain.transactiontype;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.orbenox.erp.config.CacheConfig.CacheNames.LOOKUPS;
import static com.orbenox.erp.config.CacheConfig.CacheNames.TRANSACTION_TYPES;

@Service
@RequiredArgsConstructor
public class TransactionTypeService {
    private final TransactionTypeRepository transactionTypeRepository;
    private final TransactionTypeMapper transactionTypeMapper;

    @Cacheable(TRANSACTION_TYPES)
    public List<TransactionTypeItem> getAllItems() {
        return transactionTypeRepository.getAllItems();
    }

    public TransactionTypeItem getItemById(Long id) {
        return transactionTypeRepository.getItemById(id);
    }

    @CacheEvict(value = {TRANSACTION_TYPES, LOOKUPS}, allEntries = true)
    public TransactionTypeItem create(TransactionTypeCreateDto dto) {
        TransactionType transactionType = transactionTypeRepository.save(transactionTypeMapper.toEntity(dto));
        return transactionTypeRepository.getItemById(transactionType.getId());
    }

    @CacheEvict(value = {TRANSACTION_TYPES, LOOKUPS}, allEntries = true)
    @Transactional
    public TransactionTypeItem update(Long id, TransactionTypeUpdateDto dto) {
        TransactionType entity = transactionTypeRepository.findByIdAndDeletedFalse(id);
        transactionTypeMapper.updateEntityFromDto(dto, entity);
        return transactionTypeRepository.getItemById(id);
    }

    @CacheEvict(value = {TRANSACTION_TYPES, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        TransactionType entity = transactionTypeRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}


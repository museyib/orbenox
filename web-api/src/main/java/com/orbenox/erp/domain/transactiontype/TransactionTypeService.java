package com.orbenox.erp.domain.transactiontype;

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
public class TransactionTypeService {
    private final TransactionTypeRepository transactionTypeRepository;
    private final TransactionTypeMapper transactionTypeMapper;

    @Cacheable(TRANSACTION_TYPES)
    public Slice<TransactionTypeItem> getAllItems(int page, int size, String search) {
        if (isEmpty(search))
            return transactionTypeRepository.getAllItems(PageRequest.of(page, size));
        return transactionTypeRepository.getItemsSearched(PageRequest.of(page, size), search);
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





package com.orbenox.erp.domain.account;

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
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Cacheable(ACCOUNTS)
    public Slice<AccountItem> getAllItems(int page, int size, String search) {
        if (isEmpty(search))
            return accountRepository.getAllItems(PageRequest.of(page, size));
        return accountRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public AccountItem getItemById(Long id) {
        return accountRepository.getItemById(id);
    }

    @CacheEvict(value = {ACCOUNTS, LOOKUPS}, allEntries = true)
    public AccountItem create(AccountCreateDto dto) {
        Account account = accountRepository.save(accountMapper.toEntity(dto));
        return accountRepository.getItemById(account.getId());
    }

    @CacheEvict(value = {ACCOUNTS, LOOKUPS}, allEntries = true)
    @Transactional
    public AccountItem update(Long id, AccountUpdateDto dto) {
        Account account = accountRepository.findByIdAndDeletedFalse(id);
        accountMapper.updateEntityFromDto(dto, account);
        return accountRepository.getItemById(id);
    }

    @CacheEvict(value = {ACCOUNTS, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Account account = accountRepository.findByIdAndDeletedFalse(id);
        account.setDeleted(true);
    }
}




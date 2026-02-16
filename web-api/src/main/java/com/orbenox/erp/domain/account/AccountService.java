package com.orbenox.erp.domain.account;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.orbenox.erp.config.CacheConfig.CacheNames.ACCOUNTS;
import static com.orbenox.erp.config.CacheConfig.CacheNames.LOOKUPS;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Cacheable(ACCOUNTS)
    public List<AccountItem> getAllItems() {
        return accountRepository.getAllItems();
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

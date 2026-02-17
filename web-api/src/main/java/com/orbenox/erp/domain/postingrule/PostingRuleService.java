package com.orbenox.erp.domain.postingrule;

import com.orbenox.erp.domain.account.AccountRepository;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
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
public class PostingRuleService {
    private final PostingRuleRepository postingRuleRepository;
    private final PostingRuleMapper postingRuleMapper;
    private final TransactionTypeRepository transactionTypeRepository;
    private final AccountRepository accountRepository;

    @Cacheable(POSTING_RULES)
    public Slice<PostingRuleItem> getAllItems(int page, int size, String search) {
        if (isEmpty(search))
            return postingRuleRepository.getAllItems(PageRequest.of(page, size));
        return postingRuleRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public PostingRuleItem getItemById(Long id) {
        return postingRuleRepository.getItemById(id);
    }

    @CacheEvict(value = POSTING_RULES, allEntries = true)
    public PostingRuleItem create(PostingRuleDto dto) {
        PostingRule postingRule = postingRuleMapper.toEntity(dto);
        assignRelations(postingRule, dto);
        PostingRule saved = postingRuleRepository.save(postingRule);
        return postingRuleRepository.getItemById(saved.getId());
    }

    @CacheEvict(value = POSTING_RULES, allEntries = true)
    @Transactional
    public PostingRuleItem update(Long id, PostingRuleDto dto) {
        PostingRule postingRule = postingRuleRepository.findById(id).orElseThrow();
        postingRuleMapper.updateEntityFromDto(dto, postingRule);
        assignRelations(postingRule, dto);
        return postingRuleRepository.getItemById(id);
    }

    @CacheEvict(value = POSTING_RULES, allEntries = true)
    public void delete(Long id) {
        postingRuleRepository.deleteById(id);
    }

    private void assignRelations(PostingRule postingRule, PostingRuleDto dto) {
        postingRule.setType(transactionTypeRepository.getReferenceById(dto.transactionTypeId()));
        postingRule.setDebitAccount(accountRepository.getReferenceById(dto.debitAccountId()));
        postingRule.setCreditAccount(accountRepository.getReferenceById(dto.creditAccountId()));
    }
}




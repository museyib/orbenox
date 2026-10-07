package com.orbenox.erp.domain.postingrule;

import com.orbenox.erp.domain.account.Account;
import com.orbenox.erp.domain.account.AccountRepository;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.domain.transactiontype.TransactionTypeRepository;
import com.orbenox.erp.enums.AmountSource;
import com.orbenox.erp.enums.PartnerSide;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PostingRuleServiceTest {

    private final PostingRuleRepository repository = mock(PostingRuleRepository.class);
    private final PostingRuleMapper mapper = mock(PostingRuleMapper.class);
    private final TransactionTypeRepository transactionTypeRepository = mock(TransactionTypeRepository.class);
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final PostingRuleService service =
            new PostingRuleService(repository, mapper, transactionTypeRepository, accountRepository);

    @Test
    void getAllItems_whenSearchIsBlank_shouldUseUnfilteredQuery() {
        Slice<PostingRuleItem> items = mock(Slice.class);
        when(repository.getAllItems(PageRequest.of(0, 10))).thenReturn(items);

        assertThat(service.getAllItems(0, 10, "  ")).isSameAs(items);
    }

    @Test
    void create_shouldAssignTypeAndAccountsBeforeSaving() {
        PostingRuleDto dto = dto();
        PostingRule entity = new PostingRule();
        PostingRuleItem item = mock(PostingRuleItem.class);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(transactionTypeRepository.getReferenceById(1L)).thenReturn(new TransactionType());
        when(accountRepository.getReferenceById(2L)).thenReturn(new Account());
        when(accountRepository.getReferenceById(3L)).thenReturn(new Account());
        when(repository.save(entity)).thenAnswer(invocation -> {
            entity.setId(4L);
            return entity;
        });
        when(repository.getItemById(4L)).thenReturn(item);

        assertThat(service.create(dto)).isSameAs(item);
        assertThat(entity.getType()).isNotNull();
        assertThat(entity.getDebitAccount()).isNotNull();
        assertThat(entity.getCreditAccount()).isNotNull();
    }

    @Test
    void update_shouldUpdateEntityRelations() {
        PostingRuleDto dto = dto();
        PostingRule entity = new PostingRule();
        PostingRuleItem item = mock(PostingRuleItem.class);
        when(repository.findById(4L)).thenReturn(Optional.of(entity));
        when(transactionTypeRepository.getReferenceById(1L)).thenReturn(new TransactionType());
        when(accountRepository.getReferenceById(2L)).thenReturn(new Account());
        when(accountRepository.getReferenceById(3L)).thenReturn(new Account());
        when(repository.getItemById(4L)).thenReturn(item);

        assertThat(service.update(4L, dto)).isSameAs(item);
        verify(mapper).updateEntityFromDto(dto, entity);
        assertThat(entity.getType()).isNotNull();
        assertThat(entity.getDebitAccount()).isNotNull();
        assertThat(entity.getCreditAccount()).isNotNull();
    }

    @Test
    void delete_shouldDeleteRuleById() {
        service.delete(4L);

        verify(repository).deleteById(4L);
    }

    private PostingRuleDto dto() {
        return new PostingRuleDto(1, 1L, 2L, 3L, AmountSource.NET, PartnerSide.DEBIT);
    }
}

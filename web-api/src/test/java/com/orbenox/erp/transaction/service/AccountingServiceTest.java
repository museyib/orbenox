package com.orbenox.erp.transaction.service;

import com.orbenox.erp.domain.account.Account;
import com.orbenox.erp.domain.postingrule.PostingRule;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.enums.AmountSource;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.JournalLine;
import com.orbenox.erp.transaction.repository.JournalEntryRepository;
import com.orbenox.erp.transaction.repository.JournalLineRepository;
import com.orbenox.erp.transaction.resolver.AmountResolver;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AccountingServiceTest {

    @Test
    void post_shouldCreateBalancedJournalLinesForEachNonzeroPostingRule() {
        JournalEntryRepository entryRepository = mock(JournalEntryRepository.class);
        JournalLineRepository lineRepository = mock(JournalLineRepository.class);
        AmountResolver amountResolver = mock(AmountResolver.class);
        when(entryRepository.save(12L, "POSTED")).thenReturn(2);
        Account debitAccount = new Account();
        Account creditAccount = new Account();
        PostingRule rule = new PostingRule();
        rule.setDebitAccount(debitAccount);
        rule.setCreditAccount(creditAccount);
        rule.setAmountSource(AmountSource.NET);
        Document document = new Document();
        document.setId(12L);
        TransactionType type = new TransactionType();
        type.setRules(Set.of(rule));
        document.setType(type);
        when(amountResolver.resolve(rule, document)).thenReturn(new BigDecimal("45.25"));

        new AccountingService(entryRepository, lineRepository, amountResolver).post(document);

        ArgumentCaptor<JournalLine> captor = ArgumentCaptor.forClass(JournalLine.class);
        verify(lineRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).hasSize(2);
        JournalLine debit = captor.getAllValues().get(0);
        JournalLine credit = captor.getAllValues().get(1);
        assertThat(debit.getAccount()).isSameAs(debitAccount);
        assertThat(debit.getDebit()).isEqualByComparingTo("45.25");
        assertThat(debit.getCredit()).isEqualByComparingTo("0");
        assertThat(credit.getAccount()).isSameAs(creditAccount);
        assertThat(credit.getDebit()).isEqualByComparingTo("0");
        assertThat(credit.getCredit()).isEqualByComparingTo("45.25");
        assertThat(debit.getJournalEntry()).isSameAs(credit.getJournalEntry());
        verify(entryRepository).save(12L, "POSTED");
    }

    @Test
    void post_shouldSkipZeroAmountsAndStopWhenJournalInsertDidNotAffectMultipleRows() {
        JournalEntryRepository entryRepository = mock(JournalEntryRepository.class);
        JournalLineRepository lineRepository = mock(JournalLineRepository.class);
        AmountResolver amountResolver = mock(AmountResolver.class);
        PostingRule zeroRule = new PostingRule();
        PostingRule nonzeroRule = new PostingRule();
        Document document = new Document();
        TransactionType type = new TransactionType();
        type.setRules(Set.of(zeroRule, nonzeroRule));
        document.setType(type);
        when(entryRepository.save(null, "POSTED")).thenReturn(2);
        when(amountResolver.resolve(zeroRule, document)).thenReturn(BigDecimal.ZERO);
        when(amountResolver.resolve(nonzeroRule, document)).thenReturn(new BigDecimal("1"));

        new AccountingService(entryRepository, lineRepository, amountResolver).post(document);

        verify(amountResolver).resolve(zeroRule, document);
        verify(amountResolver).resolve(nonzeroRule, document);
        verify(lineRepository, times(2)).save(any(JournalLine.class));

        reset(entryRepository, lineRepository, amountResolver);
        when(entryRepository.save(null, "POSTED")).thenReturn(1);
        new AccountingService(entryRepository, lineRepository, amountResolver).post(document);
        verifyNoInteractions(amountResolver, lineRepository);
    }
}

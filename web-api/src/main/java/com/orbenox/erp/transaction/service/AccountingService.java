package com.orbenox.erp.transaction.service;

import com.orbenox.erp.domain.postingrule.PostingRule;
import com.orbenox.erp.enums.JournalStatus;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.JournalEntry;
import com.orbenox.erp.transaction.entity.JournalLine;
import com.orbenox.erp.transaction.repository.JournalEntryRepository;
import com.orbenox.erp.transaction.repository.JournalLineRepository;
import com.orbenox.erp.transaction.resolver.AmountResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountingService implements ContextService {
    private final JournalEntryRepository journalEntryRepo;
    private final JournalLineRepository journalLineRepo;
    private final AmountResolver amountResolver;

    @Override
    public void post(Document doc) {
        JournalEntry entry = new JournalEntry();
        entry.setDocument(doc);
        entry.setStatus(JournalStatus.POSTED);
        journalEntryRepo.save(entry);

        Set<PostingRule> rules = doc.getType().getRules();
        for (PostingRule rule : rules) {
            applyPostingRule(rule, doc, entry);
        }
    }

    private void applyPostingRule(PostingRule rule, Document doc, JournalEntry je) {
        BigDecimal amount = amountResolver.resolve(rule, doc);

        JournalLine debit = new JournalLine();
        debit.setJournalEntry(je);
        debit.setAccount(rule.getDebitAccount());
        debit.setDebit(amount);
        debit.setCredit(BigDecimal.ZERO);
        journalLineRepo.save(debit);

        JournalLine credit = new JournalLine();
        credit.setJournalEntry(je);
        credit.setAccount(rule.getCreditAccount());
        credit.setDebit(BigDecimal.ZERO);
        credit.setCredit(amount);
        journalLineRepo.save(credit);
    }
}

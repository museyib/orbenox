package com.orbenox.erp.domain.transactiontype.numbering;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.enums.ResetPeriod;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DocumentNumberGenerator {
    private final DocumentSequenceRepository documentSequenceRepo;
    private final NumberingPolicyRepository numberingPolicyRepo;

    @Transactional
    public String generate(TransactionType type, LocalDate date) {
        NumberingPolicy policy = numberingPolicyRepo.findByTypeId(type.getId())
                .orElseThrow();

        int year = resolveYear(policy, date);
        int month = resolveMonth(policy, date);

        DocumentSequence sequence = documentSequenceRepo
                .findForUpdate(type.getId(), year, month)
                .orElseGet(() -> createNewSequence(type, year, month));

        long value = sequence.getNextValue();
        sequence.setNextValue(value + 1);

        return buildNumber(policy, date, value);
    }

    private String buildNumber(NumberingPolicy policy, LocalDate date, long value) {
        String yy = String.valueOf(date.getYear()).substring(2);
        String mm = String.format("%02d", date.getMonthValue());

        String seq = String.format("%0" + policy.getSequenceLength() + "d", value);

        return policy.getPrefix() + yy + mm + seq;
    }

    private DocumentSequence createNewSequence(TransactionType type, int year,  int month) {
        DocumentSequence sequence = new DocumentSequence();
        sequence.setType(type);
        sequence.setYear(year);
        sequence.setMonth(month);
        sequence.setNextValue(1L);
        return documentSequenceRepo.save(sequence);
    }

    private int resolveYear(NumberingPolicy policy, LocalDate date) {
        return switch (policy.getResetPeriod()) {
            case MONTHLY, YEARLY -> date.getYear();
            case NEVER -> 0;
        };
    }

    private int resolveMonth(NumberingPolicy policy, LocalDate date) {
        return policy.getResetPeriod() == ResetPeriod.MONTHLY
                ? date.getMonth().getValue()
                : 0;
    }
}

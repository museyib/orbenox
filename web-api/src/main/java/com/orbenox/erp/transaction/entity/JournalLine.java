package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.domain.account.Account;
import com.orbenox.erp.domain.businesspartner.BusinessPartner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.math.BigDecimal.ZERO;

@Getter
@Setter
@Entity
public class JournalLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private JournalEntry journalEntry;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private BusinessPartner partner;

    @Column(nullable = false)
    private BigDecimal debit;

    @Column(nullable = false)
    private BigDecimal credit;

    @PrePersist
    @PreUpdate
    private void validate() {
        String key = "error.journalLine.invalidDebitCredit";
        String message = null;
        Locale locale = LocaleContextHolder.getLocale();
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", locale);
            if (bundle.containsKey(key)) {
                message = bundle.getString(key);
            }
        } catch (MissingResourceException ignored) {
            message = key;
        }
        if ((debit.equals(ZERO) && credit.equals(ZERO))
                || (!debit.equals(ZERO) && !Objects.equals(credit, ZERO)))
            throw new IllegalStateException(message);
    }
}
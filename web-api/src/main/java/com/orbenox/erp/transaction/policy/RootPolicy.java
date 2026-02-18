package com.orbenox.erp.transaction.policy;

import com.orbenox.erp.domain.transactiontype.TransactionType;

public interface RootPolicy {
    boolean supports(TransactionType type);
}
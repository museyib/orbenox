package com.orbenox.erp.domain.postingrule;

import com.orbenox.erp.enums.AmountSource;
import com.orbenox.erp.enums.PartnerSide;

public interface PostingRuleItem {
    Long getId();
    Integer getSequence();
    Long getTransactionTypeId();
    String getTransactionTypeCode();
    Long getDebitAccountId();
    String getDebitAccountCode();
    Long getCreditAccountId();
    String getCreditAccountCode();
    AmountSource getAmountSource();
    PartnerSide getPartnerSide();
}

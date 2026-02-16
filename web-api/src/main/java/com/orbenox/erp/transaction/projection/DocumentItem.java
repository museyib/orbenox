package com.orbenox.erp.transaction.projection;

import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;

import java.time.LocalDate;

public interface DocumentItem {
    Long getId();
    String getNumber();
    LocalDate getDocumentDate();
    String getDescription();
    DocumentStatus getDocumentStatus();
    ApprovalStatus getApprovalStatus();
    Long getTypeId();
    String getTypeCode();
    String getTypeName();
}


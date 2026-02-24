package com.orbenox.erp.transaction.projection;

import com.orbenox.erp.domain.businesspartner.BusinessPartnerItem;
import com.orbenox.erp.domain.price.PriceListItem;
import com.orbenox.erp.domain.transactiontype.SimpleTransactionTypeItem;
import com.orbenox.erp.domain.warehouse.WarehouseItem;
import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;

import java.time.LocalDate;

public interface DocumentItem {
    Long getId();
    String getDocumentNo();
    LocalDate getDocumentDate();
    String getDescription();
    DocumentStatus getDocumentStatus();
    ApprovalStatus getApprovalStatus();
    SimpleTransactionTypeItem getTypeItem();
    Long getPartnerId();
    WarehouseItem getSourceWarehouse();
    WarehouseItem getTargetWarehouse();
    BusinessPartnerItem getBusinessPartner();
    PriceListItem getPriceList();
}


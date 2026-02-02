package com.orbenox.erp.transaction.service;

import com.orbenox.erp.domain.businesspartner.BusinessPartner;
import com.orbenox.erp.transaction.entity.CommercialContext;
import com.orbenox.erp.transaction.entity.Document;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
public class AccountingService implements ContextService {
    @Override
    public void post(Document doc) {
        CommercialContext cc = doc.getCommercialContext();

        BigDecimal total  = doc.getProductLines().stream()
                .map(l -> l.getUnitPrice()
                        .multiply(l.getQuantity())
                        .subtract(l.getDiscount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void createDebit(Document document, BigDecimal total, BusinessPartner partner) {

    }
}

package com.orbenox.erp.transaction.policy.approval;

import com.orbenox.erp.domain.product.repository.ProductPriceRepository;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.repository.ProductLineRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(1)
@RequiredArgsConstructor
public class SalesOrderApprovalPolicy implements ApprovalPolicy {
    private final ProductPriceRepository productPriceRepo;
    private final ProductLineRepository productLineRepo;

    @Override
    public boolean supports(TransactionType type) {
        return "SALES_ORDER".equals(type.getCode());
    }

    @Override
    public boolean requiresApproval(@NonNull Document document) {
        if (document.getType().isApprovalRequired()) {
            boolean discountExceeds = false;
            if (document.getCommercialContext() != null)
                discountExceeds = productLineRepo.findAllByDocumentId((document.getId())).stream()
                    .anyMatch(line -> {
                        BigDecimal discountLimit = productPriceRepo.findByProductAndPriceList(
                                line.getProduct(),
                                document.getCommercialContext().getPriceList()).getDiscountRatioLimit();
                        return line.getDiscount().compareTo(discountLimit) > 0;
                    });
            return discountExceeds;
        }
        return false;
    }
}
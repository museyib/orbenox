package com.orbenox.erp.transaction.policy;

import com.orbenox.erp.domain.product.repository.ProductPriceRepository;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.repository.ProductLineRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class SalesOrderApprovalPolicy implements ApprovalPolicy {
    private final ProductPriceRepository productPriceRepo;
    private final ProductLineRepository productLineRepo;

    @Override
    public boolean requiresApproval(@NonNull Document document) {
        if (!document.getType().getCode().equals("SALES_ORDER")) {
            return false;
        }

        if (document.getType().isRequiresApproval()) {
            boolean allDiscountsOK = productLineRepo.findAllByDocumentId((document.getId())).stream()
                    .allMatch(line -> {
                        BigDecimal discountLimit = productPriceRepo.findByProductAndPriceList(
                                line.getProduct(),
                                document.getCommercialContext().getPriceList()).getDiscountRatioLimit();
                        return line.getDiscount().compareTo(discountLimit) <= 0;
                    });
            return !allDiscountsOK;
        }
        return false;
    }
}

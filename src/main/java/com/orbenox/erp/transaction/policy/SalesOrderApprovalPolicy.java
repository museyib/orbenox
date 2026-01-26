package com.orbenox.erp.transaction.policy;

import com.orbenox.erp.domain.product.repository.ProductPriceRepository;
import com.orbenox.erp.transaction.entity.Document;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class SalesOrderApprovalPolicy implements ApprovalPolicy {
    private final ProductPriceRepository productPriceRepository;

    @Override
    public boolean requiresApproval(@NonNull Document document) {
        if (!document.getType().getCode().equals("SALES_ORDER")) {
            return false;
        }

        if (document.getType().isRequiresApproval()) {
            boolean allDiscountsOK = document.getProductLines().stream()
                    .allMatch(line -> {
                        BigDecimal discountLimit = productPriceRepository.findByProductAndPriceList(
                                line.getProduct(),
                                document.getCommercialContext().getPriceList()).getDiscountRatioLimit();
                        return line.getDiscount().compareTo(discountLimit) <= 0;
                    });
            return !allDiscountsOK;
        }
        return false;
    }
}

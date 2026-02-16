package com.orbenox.erp.transaction.resolver;

import com.orbenox.erp.domain.postingrule.PostingRule;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.ProductLine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class AmountResolver {

    public BigDecimal resolve(PostingRule rule, Document doc) {
        return switch (rule.getAmountSource()) {
            case NET -> doc.getProductLines().stream()
                    .map(l -> l.getUnitPrice().multiply(l.getQuantity()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            case DISCOUNT -> doc.getProductLines().stream()
                    .map(ProductLine::getDiscount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            case TOTAL -> doc.getProductLines().stream()
                    .map(l -> l.getUnitPrice()
                            .multiply(l.getQuantity())
                            .subtract(BigDecimal.valueOf(100).subtract(l.getDiscount())
                                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        };
    }
}

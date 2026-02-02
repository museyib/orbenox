package com.orbenox.erp.transaction.resolver;

import com.orbenox.erp.domain.postingrule.PostingRule;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.ProductLine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
                            .subtract(l.getDiscount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        };
    }
}

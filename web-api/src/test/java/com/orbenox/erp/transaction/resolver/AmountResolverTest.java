package com.orbenox.erp.transaction.resolver;

import com.orbenox.erp.domain.postingrule.PostingRule;
import com.orbenox.erp.enums.AmountSource;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.ProductLine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AmountResolverTest {
    private final AmountResolver resolver = new AmountResolver();

    private static PostingRule rule(AmountSource source) {
        PostingRule rule = new PostingRule();
        rule.setAmountSource(source);
        return rule;
    }

    private static Document document(ProductLine... lines) {
        Document document = new Document();
        document.setProductLines(List.of(lines));
        return document;
    }

    private static ProductLine line(String unitPrice, String quantity, String discount) {
        ProductLine line = new ProductLine();
        line.setUnitPrice(new BigDecimal(unitPrice));
        line.setQuantity(new BigDecimal(quantity));
        line.setDiscount(new BigDecimal(discount));
        return line;
    }

    @Test
    void resolveNet_shouldSumPriceTimesQuantityWithoutApplyingDiscount() {
        Document document = document(line("10", "2", "25"), line("3.50", "4", "10"));

        BigDecimal amount = resolver.resolve(rule(AmountSource.NET), document);

        assertThat(amount).isEqualByComparingTo("34.00");
    }

    @Test
    void resolveDiscount_shouldSumDiscountValues() {
        Document document = document(line("10", "2", "1.25"), line("3", "1", "0.75"));

        assertThat(resolver.resolve(rule(AmountSource.DISCOUNT), document))
                .isEqualByComparingTo("2.00");
    }

    @Test
    void resolveTotal_shouldApplyDiscountAndIgnoreNonPositiveUnitPrices() {
        Document document = document(line("100", "2", "100"), line("20", "1", "0"),
                line("-5", "4", "50"));

        assertThat(resolver.resolve(rule(AmountSource.TOTAL), document))
                .isEqualByComparingTo("20");
    }
}

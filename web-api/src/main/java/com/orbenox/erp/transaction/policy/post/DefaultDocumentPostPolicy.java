package com.orbenox.erp.transaction.policy.post;

import com.orbenox.erp.messaging.command.PostDocumentCommand;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.service.AccountingService;
import com.orbenox.erp.transaction.service.CommercialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order
@Slf4j
@RequiredArgsConstructor
public class DefaultDocumentPostPolicy implements DocumentPostPolicy {
    private final AccountingService accountingService;
    private final CommercialService commercialService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public boolean supports(TransactionType type) {
        return true;
    }

    @Override
    public void post(Document document) {
        if (document.getType().isAccountingAffected()) {
            accountingService.post(document);
        }

        if (document.getType().isStockAffected()) {
            PostDocumentCommand postDocumentCommand = new PostDocumentCommand(
                    document.getId(),
                    document.getDocumentNo(),
                    document.getDocumentDate().toString(),
                    document.getDocumentStatus().name(),
                    document.getApprovalStatus().name(),
                    document.getDescription(),
                    document.getType().getCode()
            );

            rabbitTemplate.convertAndSend("stock.post", postDocumentCommand);
        }

        if (document.getType().isCommercialAffected()) {
            commercialService.post(document);
        }
    }

    protected boolean allQuantitiesPositive(Document document) {
        return document.getProductLines()
                .stream()
                .allMatch(line -> line.getQuantity().compareTo(BigDecimal.ZERO) > 0);
    }
}

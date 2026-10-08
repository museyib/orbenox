package com.orbenox.erp.transaction.service;

import com.orbenox.erp.messaging.command.StockMovementCommand;
import com.orbenox.erp.messaging.command.StockMovementCommand.StockOperation;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.entity.ProductLine;
import com.orbenox.erp.transaction.entity.StockContext;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.orbenox.erp.config.RabbitMqConfiguration.STOCK_POST_QUEUE;

@Service
@RequiredArgsConstructor
public class StockPostingService {
    private final RabbitTemplate rabbitTemplate;

    public void post(Document document) {
        if (document.getType().isStockAffected()) {
            StockContext sc = document.getStockContext();
            if (sc != null) {
                List<StockOperation> operations = new ArrayList<>();
                for (ProductLine line : document.getProductLines()) {
                    BigDecimal remaining = line.getQuantity().subtract(line.getPostedQuantity());
                    if (remaining.compareTo(BigDecimal.ZERO) > 0) {

                        if (sc.getSourceWarehouse() != null) {
                            operations.add(new StockOperation(line.getId(), line.getProduct().getId(), sc.getSourceWarehouse().getId(), remaining, -1));
                        }
                        if (sc.getTargetWarehouse() != null) {
                            operations.add(new StockOperation(line.getId(), line.getProduct().getId(), sc.getTargetWarehouse().getId(), remaining, 1));
                        }
                    }
                }

                StockMovementCommand command = new StockMovementCommand(document.getId(), document.getType().getCode(), operations);

                rabbitTemplate.convertAndSend(STOCK_POST_QUEUE, command);
            }
        }
    }
}

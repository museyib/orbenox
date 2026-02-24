package com.orbenox.erp.transaction.projection;

import java.util.List;

public record DocumentData(DocumentItem documentItem,
                           List<ProductLineItem> productLines) {
}

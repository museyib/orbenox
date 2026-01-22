package com.orbenox.erp.transaction.service;

import com.orbenox.erp.transaction.entity.Document;
import org.springframework.stereotype.Service;

@Service
public class PostingService {

    public void post(Document document) {
        if (document.getType().isAffectsStock())
            postStock(document);

        if (document.getType().isAffectsAR())
            postAccounting(document);
    }

    private void postStock(Document document) {

    }

    private void postAccounting(Document document) {

    }
}

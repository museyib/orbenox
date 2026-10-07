package com.orbenox.erp.transaction.service;

import com.orbenox.erp.transaction.entity.Document;

public interface ContextService {
    void post(Document doc);
}

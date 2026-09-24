package com.orbenox.erp.transaction.idempotency;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class IdempotentRecord implements Serializable {
    private Status status;
    private Object responseBody;
    private int responseStatus;

    public enum Status {
        PROCESSING,COMPLETED,FAILED
    }
}

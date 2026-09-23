package com.orbenox.erp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class IdempotentRecord implements Serializable {
    private Status status;
    private Object responseBody;
    private int responseStatus;

    enum Status {
        PROCESSING,COMPLETED,FAILED
    }
}

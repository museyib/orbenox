package com.orbenox.erp.idempotency;

import com.orbenox.erp.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "idempotency_record")
public class IdempotencyEntity extends BaseEntity {
    private String idempotencyKey;
    private String status;
    private String requestHash;
    private String responseBody;
    private Integer responseStatus;
}

package com.orbenox.erp.unitofmeasure;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link UnitOfMeasure}
 */
@Value
public class UnitDto implements Serializable {
    Long id;
    String code;
    String description;
}
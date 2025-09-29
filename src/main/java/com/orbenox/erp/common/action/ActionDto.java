package com.orbenox.erp.common.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionDto {
    private Long id;
    private String code;
    private String description;
}
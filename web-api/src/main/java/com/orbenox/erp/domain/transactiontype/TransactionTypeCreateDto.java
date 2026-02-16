package com.orbenox.erp.domain.transactiontype;

import com.orbenox.erp.domain.postingrule.PostingRuleDto;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/**
 * DTO for {@link TransactionType}
 */
public record TransactionTypeCreateDto(boolean enabled,
                                       @NotBlank(message = "{code.notBlank}") String code,
                                       @NotBlank(message = "{name.notBlank}") String name,
                                       Set<PostingRuleDto> rules) {
}
package com.orbenox.erp.domain.transactiontype;

import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.domain.postingrule.PostingRule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class TransactionType extends BaseCardEntity {
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean affectsCommercial;

    @Column(nullable = false)
    private boolean affectsStock;

    @Column(nullable = false)
    private boolean affectsAccount;

    @Column(nullable = false)
    private boolean checkCreditLimit;

    @Column(nullable = false)
    private boolean requiresApproval;

    @OneToMany(mappedBy = "type")
    private List<PostingRule> rules;
}
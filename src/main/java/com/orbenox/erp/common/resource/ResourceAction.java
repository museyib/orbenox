package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.Action;
import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ResourceAction extends BaseCardEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Action action;


}
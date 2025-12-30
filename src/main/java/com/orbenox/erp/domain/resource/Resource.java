package com.orbenox.erp.domain.resource;

import com.orbenox.erp.domain.action.Action;
import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
public class Resource extends BaseCardEntity {
    @Column(unique = true, nullable = false)
    private String code;
    private String name;

    @ManyToMany
    @JoinTable(
            name = "resource_action",
            joinColumns = @JoinColumn(name = "resource_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> actions;
}
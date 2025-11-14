package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.Action;
import com.orbenox.erp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
public class Resource extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String code;
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "resource_action",
            joinColumns = @JoinColumn(name = "resource_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> actions;
}
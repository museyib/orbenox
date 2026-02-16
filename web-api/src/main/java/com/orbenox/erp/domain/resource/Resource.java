package com.orbenox.erp.domain.resource;

import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.enums.Action;
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

    @Column(nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "resource_action",
            joinColumns = @JoinColumn(name = "resource_id")
    )
    @Column(name = "action", nullable = false)
    private Set<Action> actions;
}
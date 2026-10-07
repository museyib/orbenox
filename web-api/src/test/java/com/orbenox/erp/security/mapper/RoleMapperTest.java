package com.orbenox.erp.security.mapper;

import com.orbenox.erp.security.dto.RoleCreateDto;
import com.orbenox.erp.security.dto.RoleUpdateDto;
import com.orbenox.erp.security.entity.AppRole;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RoleMapperTest {
    private final RoleMapper mapper = Mappers.getMapper(RoleMapper.class);

    @Test
    void toEntity_shouldMapRoleFields() {
        AppRole role = mapper.toEntity(new RoleCreateDto(false, "FINANCE", "Finance"));

        assertThat(role.getCode()).isEqualTo("FINANCE");
        assertThat(role.getName()).isEqualTo("Finance");
        assertThat(role.isEnabled()).isFalse();
    }

    @Test
    void toEntityList_shouldMapEachRoleInTheInputSet() {
        Set<AppRole> roles = mapper.toEntityList(Set.of(
                new RoleUpdateDto(1L, true, "ADMIN", "Administrator"),
                new RoleUpdateDto(2L, false, "VIEWER", "Viewer")));

        assertThat(roles).extracting(AppRole::getCode).containsExactlyInAnyOrder("ADMIN", "VIEWER");
        assertThat(roles).extracting(AppRole::getName).containsExactlyInAnyOrder("Administrator", "Viewer");
        assertThat(roles).extracting(AppRole::isEnabled).containsExactlyInAnyOrder(true, false);
    }

    @Test
    void updateEntityFromDto_shouldApplyValuesIncludingDisabledState() {
        AppRole role = new AppRole();
        role.setCode("OLD");
        role.setName("Old role");
        role.setEnabled(true);

        mapper.updateEntityFromDTO(new RoleUpdateDto(7L, false, "NEW", "New role"), role);

        assertThat(role.getCode()).isEqualTo("NEW");
        assertThat(role.getName()).isEqualTo("New role");
        assertThat(role.isEnabled()).isFalse();
    }

    @Test
    void mapperMethods_shouldHandleNullInputsWithoutChangingExistingRole() {
        AppRole role = new AppRole();
        role.setCode("KEEP");

        assertThat(mapper.toEntity(null)).isNull();
        assertThat(mapper.toEntityList(null)).isNull();
        mapper.updateEntityFromDTO(null, role);

        assertThat(role.getCode()).isEqualTo("KEEP");
    }
}

package com.orbenox.erp.security.service;

import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.entity.AppRole;
import com.orbenox.erp.security.mapper.RoleMapper;
import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleItem> getAllItems() {
        return roleRepository.getAllItems();
    }

    public RoleItem getItemById(Long id) {
        return roleRepository.getItemById(id);
    }

    public RoleItem save(RoleDto roleDTO) {
        AppRole appRole = roleRepository.save(roleMapper.toEntity(roleDTO));
        return roleRepository.getItemById(appRole.getId());
    }

    @Transactional
    public RoleItem update(Long id, RoleDto request) {
        AppRole appRole = roleRepository.findByIdAndDeletedFalse(id);
        roleMapper.updateEntityFromDTO(request, appRole);
        return roleRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        AppRole appRole = roleRepository.findByIdAndDeletedFalse(id);
        appRole.setDeleted(true);
    }
}

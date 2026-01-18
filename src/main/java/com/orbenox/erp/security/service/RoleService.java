package com.orbenox.erp.security.service;

import com.orbenox.erp.security.dto.RoleCreateDto;
import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.entity.AppRole;
import com.orbenox.erp.security.mapper.RoleMapper;
import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Cacheable("roles")
    public List<RoleItem> getAllItems() {
        return roleRepository.getAllItems();
    }

    public RoleItem getItemById(Long id) {
        return roleRepository.getItemById(id);
    }

    @CacheEvict(value = "roles", allEntries = true)
    public RoleItem create(RoleCreateDto roleDTO) {
        AppRole appRole = roleRepository.save(roleMapper.toEntity(roleDTO));
        return roleRepository.getItemById(appRole.getId());
    }

    @CacheEvict(value = "roles", allEntries = true)
    @Transactional
    public RoleItem update(Long id, RoleDto request) {
        AppRole appRole = roleRepository.findByIdAndDeletedFalse(id);
        roleMapper.updateEntityFromDTO(request, appRole);
        return roleRepository.getItemById(id);
    }

    @CacheEvict(value = "roles", allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        AppRole appRole = roleRepository.findByIdAndDeletedFalse(id);
        appRole.setDeleted(true);
    }
}

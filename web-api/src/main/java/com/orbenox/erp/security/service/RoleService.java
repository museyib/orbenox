package com.orbenox.erp.security.service;

import com.orbenox.erp.security.dto.RoleCreateDto;
import com.orbenox.erp.security.dto.RoleUpdateDto;
import com.orbenox.erp.security.entity.AppRole;
import com.orbenox.erp.security.mapper.RoleMapper;
import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


import static com.orbenox.erp.config.CacheConfig.CacheNames.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Cacheable(ROLES)
    public Slice<RoleItem> getAllItems(int page, int size, String search) {
        if (isEmpty(search))
            return roleRepository.getAllItems(PageRequest.of(page, size));
        return roleRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public RoleItem getItemById(Long id) {
        return roleRepository.getItemById(id);
    }

    @CacheEvict(value = ROLES, allEntries = true)
    public RoleItem create(RoleCreateDto roleDTO) {
        AppRole appRole = roleRepository.save(roleMapper.toEntity(roleDTO));
        return roleRepository.getItemById(appRole.getId());
    }

    @CacheEvict(value = ROLES, allEntries = true)
    @Transactional
    public RoleItem update(Long id, RoleUpdateDto request) {
        AppRole appRole = roleRepository.findByIdAndDeletedFalse(id);
        roleMapper.updateEntityFromDTO(request, appRole);
        return roleRepository.getItemById(id);
    }

    @CacheEvict(value = ROLES, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        AppRole appRole = roleRepository.findByIdAndDeletedFalse(id);
        appRole.setDeleted(true);
    }
}





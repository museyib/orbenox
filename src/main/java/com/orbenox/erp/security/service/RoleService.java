package com.orbenox.erp.security.service;

import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.dto.mapper.RoleMapper;
import com.orbenox.erp.security.entity.AppRole;
import com.orbenox.erp.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleDto> findAll() {
        return roleMapper.toDTOList(roleRepository.findAllByDeletedFalse());
    }

    public RoleDto findById(Long id) {
        return roleMapper.toDTO(roleRepository.findByIdAndDeletedFalse(id));
    }

    public RoleDto save(RoleDto roleDTO) {
        AppRole appRole = roleMapper.toEntity(roleDTO);
        return roleMapper.toDTO(roleRepository.save(appRole));
    }

    public RoleDto update(Long id, RoleDto request) {
        AppRole appRole = roleRepository.findByIdAndDeletedFalse(id);
        roleMapper.updateEntityFromDTO(request, appRole);
        return roleMapper.toDTO(roleRepository.save(appRole));
    }

    public void softDelete(Long id) {
        AppRole appRole = roleRepository.findByIdAndDeletedFalse(id);
        appRole.setDeleted(true);
        AppRole saved = roleRepository.save(appRole);
        if (!saved.isDeleted()) {
            throw new IllegalStateException("Role has been deleted!");
        }
    }
}

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
        return roleMapper.toDTOList(roleRepository.findAll());
    }

    public RoleDto findById(Long id) {
        return roleMapper.toDTO(roleRepository.findById(id).orElseThrow());
    }

    public RoleDto save(RoleDto roleDTO) {
        if (roleNameExists(roleDTO.getRoleName())) {
            throw new IllegalStateException("Role name already exists!");
        }
        AppRole appRole = roleMapper.toEntity(roleDTO);
        return roleMapper.toDTO(roleRepository.save(appRole));
    }

    public RoleDto update(Long id, RoleDto request) {
        AppRole appRole = roleRepository.findById(id).orElseThrow();
        roleMapper.updateEntityFromDTO(request, appRole);
        return roleMapper.toDTO(roleRepository.save(appRole));
    }

    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    public boolean roleNameExists(String roleName) {
        return roleRepository.findByRoleName(roleName).isPresent();
    }
}

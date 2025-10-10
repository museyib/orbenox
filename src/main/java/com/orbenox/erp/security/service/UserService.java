package com.orbenox.erp.security.service;

import com.orbenox.erp.exception.AlterAdminException;
import com.orbenox.erp.security.dto.CreateUserRequest;
import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.dto.UpdateUserRequest;
import com.orbenox.erp.security.dto.UserDto;
import com.orbenox.erp.security.dto.mapper.UserMapper;
import com.orbenox.erp.security.entity.AppRole;
import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.entity.AppUserRole;
import com.orbenox.erp.security.repository.AppUserRoleRepository;
import com.orbenox.erp.security.repository.RoleRepository;
import com.orbenox.erp.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.orbenox.erp.security.enums.UserType.ADMIN;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AppUserRoleRepository  appUserRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public List<UserDto> findAll() {
        return userMapper.toDTOList(userRepository.findByRoot((false)));
    }

    public UserDto findById(Long id) {
        return userMapper.toDTO(userRepository.findByIdAndRoot(id, false).orElseThrow());
    }

    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public UserDto create(CreateUserRequest request) {
        if (usernameExists(request.getUsername())) {
            throw new IllegalStateException("Username already exists!");
        }
        AppUser appUser = userMapper.toEntity(request);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return userMapper.toDTO(userRepository.save(appUser));
    }

    public UserDto update(Long id, UpdateUserRequest request) {
        AppUser appUser = userRepository.findById(id).orElseThrow();
        if (appUser.isRoot()) {
            if (request.getUserType().equals(ADMIN.name()))
                throw new AlterAdminException("Changing user type of root user  is not allowed!");
        }
        userMapper.updateEntityFromDTO(request,appUser);
        Set<RoleDto> incomingRoles = request.getRoles();
        Set<RoleDto> existingRoles = userMapper.toDTO(appUser).getRoles();
        Set<RoleDto> toRemove = new HashSet<>(existingRoles);
        Set<RoleDto> toAdd = new HashSet<>(incomingRoles);
        toRemove.removeAll(incomingRoles);
        toAdd.removeAll(existingRoles);
        appUserRoleRepository.deleteByAppUserIdAndAppRole(id, toRemove.stream().map(RoleDto::getId).collect(Collectors.toSet()));

        List<AppUserRole> userRoles = request.getRoles().stream()
                .filter(toAdd::contains)
                .map(roleDto -> {
                    AppUserRole appUserRole = new AppUserRole();
                    AppRole appRole = roleRepository.findById(roleDto.getId()).orElseThrow();
                    appUserRole.setAppUser(appUser);
                    appUserRole.setAppRole(appRole);
                    return appUserRole;
                }).toList();
        if (!userRoles.isEmpty())
            appUserRoleRepository.saveAll(userRoles);

        if (request.getRoles() != null) {
            Set<AppRole> roles = request.getRoles().stream()
                    .map(roleDTO -> roleRepository.findById(roleDTO.getId())
                            .orElseThrow())
                    .collect(Collectors.toSet());
            appUser.setRoles(roles);
        }
        return userMapper.toDTO(userRepository.save(appUser));
    }

    public void delete(Long id) {
        AppUser appUser = userRepository.findById(id).orElseThrow();
        if (appUser.getUsername().equals("admin")) {
            throw new AlterAdminException("Deleting root user is not allowed!");
        }
        userRepository.deleteById(id);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}

package com.orbenox.erp.security.service;

import com.orbenox.erp.exception.AlterRootException;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.security.dto.CreateUserRequest;
import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.dto.UpdateUserRequest;
import com.orbenox.erp.security.dto.UserDto;
import com.orbenox.erp.security.mapper.UserMapper;
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
    private final LocalizationService i18n;

    public List<UserDto> findAll() {
        return userMapper.toDTOList(userRepository.findByRootFalseAndDeletedFalse());
    }

    public UserDto findById(Long id) {
        return userMapper.toDTO(userRepository.findByIdAndRootFalseAndDeletedFalse(id).orElseThrow());
    }

    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public UserDto create(CreateUserRequest request) {
        AppUser appUser = userMapper.toEntity(request);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return userMapper.toDTO(userRepository.save(appUser));
    }

    public UserDto update(Long id, UpdateUserRequest request) {
        AppUser appUser = userRepository.findByIdAndDeletedFalse(id);
        if (appUser.isRoot()) {
            if (request.getUserType().equals(ADMIN.name()))
                throw new AlterRootException(i18n.msg("error.alterRoot"));
        }
        userMapper.updateEntityFromDTO(request,appUser);
        Set<RoleDto> incomingRoles = request.getRoles();
        Set<RoleDto> existingRoles = userMapper.toDTO(appUser).roles();
        Set<RoleDto> toRemove = new HashSet<>(existingRoles);
        Set<RoleDto> toAdd = new HashSet<>(incomingRoles);
        toRemove.removeAll(incomingRoles);
        toAdd.removeAll(existingRoles);
        appUserRoleRepository.deleteByAppUserIdAndAppRole(id, toRemove.stream().map(RoleDto::id).collect(Collectors.toSet()));

        List<AppUserRole> userRoles = request.getRoles().stream()
                .filter(toAdd::contains)
                .map(roleDto -> {
                    AppUserRole appUserRole = new AppUserRole();
                    AppRole appRole = roleRepository.findByIdAndDeletedFalse(roleDto.id());
                    appUserRole.setAppUser(appUser);
                    appUserRole.setAppRole(appRole);
                    return appUserRole;
                }).toList();
        if (!userRoles.isEmpty())
            appUserRoleRepository.saveAll(userRoles);

        if (request.getRoles() != null) {
            Set<AppRole> roles = request.getRoles().stream()
                    .map(roleDTO -> roleRepository.findByIdAndDeletedFalse(roleDTO.id()))
                    .collect(Collectors.toSet());
            appUser.setRoles(roles);
        }
        return userMapper.toDTO(userRepository.save(appUser));
    }

    public void delete(Long id) {
        AppUser appUser = userRepository.findByIdAndDeletedFalse(id);
        if (appUser.getUsername().equals("admin")) {
            throw new AlterRootException(i18n.msg("error.deleteRoot"));
        }
        appUser.setDeleted(true);
        AppUser deleted = userRepository.save(appUser);
        if (!deleted.getDeleted())
            throw new IllegalStateException(i18n.msg("error.internal"));
    }
}

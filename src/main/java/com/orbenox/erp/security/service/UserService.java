package com.orbenox.erp.security.service;

import com.orbenox.erp.exception.AlterRootException;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.security.dto.CreateUserRequest;
import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.dto.UpdateUserRequest;
import com.orbenox.erp.security.dto.UserData;
import com.orbenox.erp.security.entity.AppRole;
import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.entity.AppUserRole;
import com.orbenox.erp.security.mapper.RoleMapper;
import com.orbenox.erp.security.mapper.UserMapper;
import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.projection.UserItem;
import com.orbenox.erp.security.repository.AppUserRoleRepository;
import com.orbenox.erp.security.repository.RoleRepository;
import com.orbenox.erp.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
    private final RoleMapper roleMapper;
    private final EntityManager em;

    public List<UserItem> getAllItems() {
        return userRepository.getAllItems();
    }

    public UserData getItemById(Long id) {
        UserItem appUser = userRepository.getItemById(id);
        List<RoleItem> roles = userRepository.getRolesByUserId(id);
        UserData userData = new UserData();
        userData.setUser(appUser);
        userData.setRoles(roles);
        return userData;
    }

    public UserItem getByUsername(String username) {
        return userRepository.getItemByUsername(username);
    }

    public UserItem create(CreateUserRequest request) {
        AppUser appUser = userMapper.toEntity(request);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setUserType(appUser.getUserType());
        appUser.setRoles(roleMapper.toEntityList(request.getRoles()));
        AppUser saved = userRepository.save(appUser);
        return userRepository.getItemById(saved.getId());
    }

    @Transactional
    public UserItem update(Long id, UpdateUserRequest request) {
        AppUser appUser = userRepository.findByIdAndDeletedFalse(id);
        if (appUser.isRoot()) {
            if (request.getUserType().equals(ADMIN.name()))
                throw new AlterRootException(i18n.msg("error.alterRoot"));
        }
        userMapper.updateEntityFromDto(request, appUser);
        Set<RoleDto> incomingRoles = request.getRoles();
        Set<RoleDto> existingRoles = userMapper.toDto(appUser).roles();
        Set<RoleDto> toRemove = new HashSet<>(existingRoles);
        Set<RoleDto> toAdd = new HashSet<>(incomingRoles);
        toRemove.removeAll(incomingRoles);
        toAdd.removeAll(existingRoles);
        appUserRoleRepository.deleteByAppUserIdAndAppRole(id, toRemove.stream().map(RoleDto::id).collect(Collectors.toSet()));

        List<AppUserRole> userRoles = request.getRoles().stream()
                .filter(toAdd::contains)
                .map(roleDto -> {
                    AppUserRole appUserRole = new AppUserRole();
                    AppRole appRole = em.getReference(AppRole.class, roleDto.id());
                    appUserRole.setAppUser(appUser);
                    appUserRole.setAppRole(appRole);
                    return appUserRole;
                }).toList();
        if (!userRoles.isEmpty())
            appUserRoleRepository.saveAll(userRoles);

        if (request.getRoles() != null) {
            Set<AppRole> roles = request.getRoles().stream()
                    .map(roleDto -> roleRepository.findByIdAndDeletedFalse(roleDto.id()))
                    .collect(Collectors.toSet());
            appUser.setRoles(roles);
        }
        return userRepository.getItemById(id);
    }

    @Transactional
    public void delete(Long id) {
        AppUser appUser = userRepository.findByIdAndDeletedFalse(id);
        if (appUser.getUsername().equals("admin")) {
            throw new AlterRootException(i18n.msg("error.deleteRoot"));
        }
        appUser.setDeleted(true);
    }
}
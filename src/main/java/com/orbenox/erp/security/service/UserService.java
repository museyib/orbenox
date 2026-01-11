package com.orbenox.erp.security.service;

import com.orbenox.erp.exception.AlterRootException;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.security.dto.UserData;
import com.orbenox.erp.security.dto.UserDto;
import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.entity.UserType;
import com.orbenox.erp.security.mapper.RoleMapper;
import com.orbenox.erp.security.mapper.UserMapper;
import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.projection.UserItem;
import com.orbenox.erp.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final LocalizationService i18n;
    private final RoleMapper roleMapper;
    private final EntityManager em;

    public List<UserItem> getAllItems() {
        return userRepository.getAllItems();
    }

    public UserData getItemById(Long id) {
        UserItem userItem = userRepository.getItemById(id);
        List<RoleItem> roles = userRepository.getRolesByUserId(id);
        UserData userData = new UserData();
        userData.setUser(userItem);
        userData.setRoles(roles);
        return userData;
    }

    public UserItem getByUsername(String username) {
        return userRepository.getItemByUsername(username);
    }

    public UserItem create(UserDto dto) {
        AppUser appUser = userMapper.toEntity(dto);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRoles(roleMapper.toEntityList(dto.roles()));
        AppUser saved = userRepository.save(appUser);
        return userRepository.getItemById(saved.getId());
    }

    @Transactional
    public UserItem update(Long id, UserDto dto) {
        AppUser appUser = userRepository.findByIdAndDeletedFalse(id);
        if (appUser.isRoot()) {
            if (dto.userType().code().equals("ADMIN"))
                throw new AlterRootException(i18n.msg("error.alterRoot"));
        }
        userMapper.updateEntityFromDto(dto, appUser);
        UserType userType = em.getReference(UserType.class, dto.userType().id());
        appUser.setUserType(userType);
        appUser.setRoles(roleMapper.toEntityList(dto.roles()));
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
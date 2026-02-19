package com.orbenox.erp.security.service;

import com.orbenox.erp.exception.AlterRootException;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.security.dto.UserCreateDto;
import com.orbenox.erp.security.dto.UserUpdateDto;
import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.mapper.UserMapper;
import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.projection.SimpleUserItem;
import com.orbenox.erp.security.projection.UserData;
import com.orbenox.erp.security.projection.UserItem;
import com.orbenox.erp.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;


import static com.orbenox.erp.config.CacheConfig.CacheNames.*;
import static com.orbenox.erp.common.Utilities.isBlank;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final LocalizationService i18n;

    @Cacheable(USERS)
    public Slice<SimpleUserItem> getAllItems(int page, int size, String search) {
        if (isBlank(search))
            return userRepository.getAllItems(PageRequest.of(page, size));
        return userRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public UserData getItemById(Long id) {
        SimpleUserItem userItem = userRepository.getItemById(id);
        List<RoleItem> roles = userRepository.getRolesByUserId(id);
        UserData userData = new UserData();
        userData.setUser(userItem);
        userData.setRoles(roles);
        return userData;
    }

    @Cacheable(USER_DETAILS)
    public UserItem getByUsername(String username) {
        return userRepository.getItemByUsername(username);
    }

    @CacheEvict(value = {USER_DETAILS, USERS}, allEntries = true)
    public SimpleUserItem create(UserCreateDto dto) {
        AppUser appUser = userMapper.toEntity(dto);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        AppUser saved = userRepository.save(appUser);
        return userRepository.getItemById(saved.getId());
    }

    @CacheEvict(value = {USER_DETAILS, USERS}, allEntries = true)
    @Transactional
    public SimpleUserItem update(Long id, UserUpdateDto dto) {
        AppUser appUser = userRepository.findByIdAndDeletedFalse(id);
        userMapper.updateEntityFromDto(dto, appUser);
        if (appUser.isRoot() && "ADMIN".equals(appUser.getUserType().getCode())) {
            throw new AlterRootException(i18n.msg("error.alterRoot"));
        }
        return userRepository.getItemById(id);
    }

    @CacheEvict(value = {USER_DETAILS, USERS}, allEntries = true)
    @Transactional
    public void delete(Long id) {
        AppUser appUser = userRepository.findByIdAndDeletedFalse(id);
        if (appUser.getUsername().equals("admin")) {
            throw new AlterRootException(i18n.msg("error.deleteRoot"));
        }
        appUser.setDeleted(true);
    }
}




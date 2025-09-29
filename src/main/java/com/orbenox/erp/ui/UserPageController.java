package com.orbenox.erp.ui;

import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.dto.UserDto;
import com.orbenox.erp.security.enums.UserType;
import com.orbenox.erp.security.service.RoleService;
import com.orbenox.erp.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class UserPageController {
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/users";
    }

    @GetMapping("/users/create")
    public String createUser() {
        return "user/create";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        UserDto userDTO = userService.findById(id);
        model.addAttribute("user", userDTO);
        List<RoleDto> roles = roleService.findAll();
        roles.removeAll(userDTO.getRoles());
        model.addAttribute("roles", roles);
        model.addAttribute("userTypes", UserType.values());
        return "user/edit";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}

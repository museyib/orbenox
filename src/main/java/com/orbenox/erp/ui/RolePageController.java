package com.orbenox.erp.ui;

import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class RolePageController {
    private final RoleService roleService;

    @GetMapping("/roles")
    public String getRoles(Model model) {
        model.addAttribute("roles", roleService.findAll());
        return "role/roles";
    }

    @GetMapping("/roles/create")
    public String createRole() {
        return "role/create";
    }

    @GetMapping("/roles/edit/{id}")
    public String editRole(@PathVariable Long id, Model model) {
        RoleDto roleDTO = roleService.findById(id);
        model.addAttribute("role", roleDTO);
        return "role/edit";
    }
}

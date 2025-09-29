package com.orbenox.erp.ui;

import com.orbenox.erp.common.action.ActionService;
import com.orbenox.erp.common.resource.ResourceService;
import com.orbenox.erp.security.dto.RolePermissionDto;
import com.orbenox.erp.security.dto.UserPermissionDto;
import com.orbenox.erp.security.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/permission")
@RequiredArgsConstructor
public class PermissionPageController {
    private final PermissionService permissionService;
    private final ResourceService resourceService;
    private final ActionService actionService;

    @GetMapping("/user/{id}")
    public String getUserPermissions(@PathVariable Long id, Model model) {
        UserPermissionDto permissionDto =  permissionService.getUserPermission(id);
        model.addAttribute("permissionDto", permissionDto);
        model.addAttribute("resources", resourceService.findAll());
        model.addAttribute("actions", actionService.findAll());
        return "/permission/user";
    }

    @GetMapping("/role/{id}")
    public String getRolePermissions(@PathVariable Long id, Model model) {
        RolePermissionDto permissionDto =  permissionService.getRolePermission(id);
        model.addAttribute("permissionDto", permissionDto);
        model.addAttribute("resources", resourceService.findAll());
        model.addAttribute("actions", actionService.findAll());
        return "/permission/role";
    }
}

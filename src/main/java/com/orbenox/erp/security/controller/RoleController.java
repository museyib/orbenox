package com.orbenox.erp.security.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.security.dto.RoleDto;
import com.orbenox.erp.security.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PreAuthorize("hasPermission('APP_ROLE', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<RoleDto>>> getAll() {
        return ResponseEntity.ok(Response.successData(roleService.findAll()));
    }

    @PreAuthorize("hasPermission('APP_ROLE', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<RoleDto>> create(@RequestBody RoleDto appRole) {
        return ResponseEntity.ok(Response.successData(roleService.save(appRole)));
    }

    @PreAuthorize("hasPermission('APP_ROLE', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<RoleDto>> updateRole(@PathVariable Long id, @RequestBody RoleDto request) {
        return ResponseEntity.ok(Response.successData(roleService.update(id, request)));
    }

    @PreAuthorize("hasPermission('APP_ROLE', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok().body(Response.successMessage("Role deleted"));
    }
}

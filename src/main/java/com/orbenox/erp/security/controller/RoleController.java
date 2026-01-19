package com.orbenox.erp.security.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.security.dto.RoleCreateDto;
import com.orbenox.erp.security.dto.RoleUpdateDto;
import com.orbenox.erp.security.projection.RoleItem;
import com.orbenox.erp.security.service.RoleService;
import jakarta.validation.Valid;
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
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('APP_ROLE', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<RoleItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(roleService.getAllItems()));
    }

    @PreAuthorize("hasPermission('APP_ROLE', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<RoleItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(roleService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('APP_ROLE', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<RoleItem>> create(@Valid @RequestBody RoleCreateDto appRole) {
        return ResponseEntity.ok(Response.successData(roleService.create(appRole)));
    }

    @PreAuthorize("hasPermission('APP_ROLE', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<RoleItem>> updateRole(@Valid @PathVariable Long id, @RequestBody RoleUpdateDto request) {
        return ResponseEntity.ok(Response.successData(roleService.update(id, request)));
    }

    @PreAuthorize("hasPermission('APP_ROLE', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        roleService.softDelete(id);
        var text = i18n.msg("role.deleted", id);
        return ResponseEntity.ok().body(Response.successMessage(text, "role.deleted"));
    }
}

package com.orbenox.erp.security.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.security.dto.CreateUserRequest;
import com.orbenox.erp.security.dto.UpdateUserRequest;
import com.orbenox.erp.security.dto.UserDto;
import com.orbenox.erp.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('APP_USER', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(Response.successData(userService.findAll()));
    }

    @PreAuthorize("hasPermission('APP_USER', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<UserDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(userService.findById(id)));
    }

    @PreAuthorize("hasPermission('APP_USER', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<UserDto>> create(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(Response.successData(userService.create(request)));
    }

    @PreAuthorize("hasPermission('APP_USER', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<UserDto>> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(Response.successData(userService.update(id, request)));
    }

    @PreAuthorize("hasPermission('APP_USER', 'DELETE')")
    @DeleteMapping("/{id}")
    public Response<String> delete(@PathVariable Long id) {
        userService.delete(id);
        var text = i18n.msg("user.deleted", id);
        return Response.successMessage(text, "user.deleted");
    }
}

package com.orbenox.erp.security.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.security.dto.UserCreateDto;
import com.orbenox.erp.security.dto.UserUpdateDto;
import com.orbenox.erp.security.projection.SimpleUserItem;
import com.orbenox.erp.security.projection.UserData;
import com.orbenox.erp.security.service.UserService;
import jakarta.validation.Valid;
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
    public ResponseEntity<Response<List<SimpleUserItem>>> getAllUsers() {
        return ResponseEntity.ok(Response.successData(userService.getAllItems()));
    }

    @PreAuthorize("hasPermission('APP_USER', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<UserData>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(userService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('APP_USER', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<SimpleUserItem>> create(@Valid @RequestBody UserCreateDto dto) {
        return ResponseEntity.ok(Response.successData(userService.create(dto)));
    }

    @PreAuthorize("hasPermission('APP_USER', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<SimpleUserItem>> update(@PathVariable Long id,
                                                     @Valid @RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(userService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('APP_USER', 'DELETE')")
    @DeleteMapping("/{id}")
    public Response<String> delete(@PathVariable Long id) {
        userService.delete(id);
        var text = i18n.msg("user.deleted", id);
        return Response.successMessage(text, "user.deleted");
    }
}

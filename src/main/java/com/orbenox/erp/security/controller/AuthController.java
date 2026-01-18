package com.orbenox.erp.security.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.security.request.LoginRequest;
import com.orbenox.erp.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(Response.successData(authService.getToken(loginRequest)));
    }
}

package com.orbenox.erp.security.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

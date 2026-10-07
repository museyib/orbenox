package com.orbenox.erp.security.controller;

import com.orbenox.erp.security.request.LoginRequest;
import com.orbenox.erp.security.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Test
    void login_shouldDelegateCredentialsAndReturnSuccessfulTokenResponse() {
        AuthService authService = mock(AuthService.class);
        AuthController controller = new AuthController(authService);
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("secret");
        when(authService.getToken(request)).thenReturn("jwt-token");

        var response = controller.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo("jwt-token");
        verify(authService).getToken(request);
    }
}

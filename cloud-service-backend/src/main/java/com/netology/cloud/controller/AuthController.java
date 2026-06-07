package com.netology.cloud.controller;

import com.netology.cloud.model.dto.LoginRequest;
import com.netology.cloud.model.dto.LoginResponse;
import com.netology.cloud.security.AuthContext;
import com.netology.cloud.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер авторизации: /login и /logout.
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String header = request.getHeader(AuthContext.AUTH_TOKEN_HEADER);
        String token = header != null && header.startsWith("Bearer ") ? header.substring(7) : header;
        authService.logout(token);
        return ResponseEntity.ok().build();
    }
}

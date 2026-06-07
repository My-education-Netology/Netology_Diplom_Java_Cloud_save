package com.netology.cloud.security;

import com.netology.cloud.exception.UnauthorizedException;
import com.netology.cloud.model.entity.User;
import com.netology.cloud.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Перехватчик проверки токена auth-token в заголовке запроса.
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader(AuthContext.AUTH_TOKEN_HEADER);
        if (header == null || header.isBlank()) {
            throw new UnauthorizedException("Неверный токен авторизации");
        }

        String token = header.startsWith("Bearer ") ? header.substring(7) : header;
        User user = authService.validateToken(token);
        request.setAttribute(AuthContext.USER_ATTRIBUTE, user);
        return true;
    }
}

package com.netology.cloud.service;

import com.netology.cloud.exception.LoginFailedException;
import com.netology.cloud.exception.UnauthorizedException;
import com.netology.cloud.model.dto.LoginErrorResponse;
import com.netology.cloud.model.dto.LoginRequest;
import com.netology.cloud.model.dto.LoginResponse;
import com.netology.cloud.model.entity.AuthToken;
import com.netology.cloud.model.entity.User;
import com.netology.cloud.repository.AuthTokenRepository;
import com.netology.cloud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сервис авторизации: вход, выход, проверка токена.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Выполняет вход по логину и паролю, возвращает токен.
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        LoginErrorResponse errors = new LoginErrorResponse();
        List<String> emailErrors = new ArrayList<>();
        List<String> passwordErrors = new ArrayList<>();

        if (request.getLogin() == null || request.getLogin().isBlank()) {
            emailErrors.add("Необходимо ввести почту");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            passwordErrors.add("Необходимо ввести пароль");
        }

        if (!emailErrors.isEmpty() || !passwordErrors.isEmpty()) {
            errors.setEmail(emailErrors);
            errors.setPassword(passwordErrors);
            throw new LoginFailedException(errors);
        }

        User user = userRepository.findByLogin(request.getLogin()).orElse(null);

        if (user == null) {
            emailErrors.add("Неправильно указана почта");
        } else if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            passwordErrors.add("Неправильно указан пароль");
        }

        if (!emailErrors.isEmpty() || !passwordErrors.isEmpty()) {
            errors.setEmail(emailErrors);
            errors.setPassword(passwordErrors);
            throw new LoginFailedException(errors);
        }

        AuthToken authToken = new AuthToken();
        authToken.setUser(user);
        authToken.setToken(UUID.randomUUID().toString().replace("-", ""));
        authTokenRepository.save(authToken);

        return new LoginResponse(authToken.getToken());
    }

    /**
     * Деактивирует токен при выходе.
     */
    @Transactional
    public void logout(String token) {
        AuthToken authToken = authTokenRepository.findByTokenAndActiveTrue(token)
                .orElseThrow(() -> new UnauthorizedException("Неверный токен авторизации"));
        authToken.setActive(false);
        authTokenRepository.save(authToken);
    }

    /**
     * Проверяет токен и возвращает пользователя.
     */
    @Transactional(readOnly = true)
    public User validateToken(String token) {
        return authTokenRepository.findByTokenAndActiveTrue(token)
                .map(AuthToken::getUser)
                .orElseThrow(() -> new UnauthorizedException("Неверный токен авторизации"));
    }
}

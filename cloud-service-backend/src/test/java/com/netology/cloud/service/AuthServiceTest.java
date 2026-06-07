package com.netology.cloud.service;

import com.netology.cloud.exception.LoginFailedException;
import com.netology.cloud.exception.UnauthorizedException;
import com.netology.cloud.model.dto.LoginRequest;
import com.netology.cloud.model.dto.LoginResponse;
import com.netology.cloud.model.entity.AuthToken;
import com.netology.cloud.model.entity.User;
import com.netology.cloud.repository.AuthTokenRepository;
import com.netology.cloud.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-тесты сервиса авторизации.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthTokenRepository authTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("test");
        testUser.setPasswordHash("hash");
    }

    @Test
    void login_shouldReturnAuthTokenOnSuccess() {
        LoginRequest request = new LoginRequest();
        request.setLogin("test");
        request.setPassword("test");

        when(userRepository.findByLogin("test")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("test", "hash")).thenReturn(true);
        when(authTokenRepository.save(any(AuthToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoginResponse response = authService.login(request);

        assertThat(response.getAuthToken()).isNotBlank();
        verify(authTokenRepository).save(any(AuthToken.class));
    }

    @Test
    void login_shouldThrowOnWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setLogin("test");
        request.setPassword("wrong");

        when(userRepository.findByLogin("test")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(LoginFailedException.class);
    }

    @Test
    void validateToken_shouldReturnUserForActiveToken() {
        AuthToken token = new AuthToken();
        token.setToken("abc");
        token.setUser(testUser);
        token.setActive(true);

        when(authTokenRepository.findByTokenAndActiveTrue("abc")).thenReturn(Optional.of(token));

        User result = authService.validateToken("abc");

        assertThat(result.getLogin()).isEqualTo("test");
    }

    @Test
    void validateToken_shouldThrowForInvalidToken() {
        when(authTokenRepository.findByTokenAndActiveTrue("bad")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.validateToken("bad"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void logout_shouldDeactivateToken() {
        AuthToken token = new AuthToken();
        token.setToken("abc");
        token.setActive(true);

        when(authTokenRepository.findByTokenAndActiveTrue("abc")).thenReturn(Optional.of(token));
        when(authTokenRepository.save(any(AuthToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.logout("abc");

        ArgumentCaptor<AuthToken> captor = ArgumentCaptor.forClass(AuthToken.class);
        verify(authTokenRepository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();
    }
}

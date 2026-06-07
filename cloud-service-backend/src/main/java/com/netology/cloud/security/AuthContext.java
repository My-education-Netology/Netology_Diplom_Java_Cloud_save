package com.netology.cloud.security;

import com.netology.cloud.model.entity.User;

/**
 * Контекст авторизованного пользователя в рамках HTTP-запроса.
 */
public final class AuthContext {

    public static final String USER_ATTRIBUTE = "currentUser";
    public static final String AUTH_TOKEN_HEADER = "auth-token";

    private AuthContext() {
    }

    public static User getCurrentUser(jakarta.servlet.http.HttpServletRequest request) {
        return (User) request.getAttribute(USER_ATTRIBUTE);
    }
}

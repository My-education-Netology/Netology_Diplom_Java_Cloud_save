package com.netology.cloud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netology.cloud.model.dto.LoginRequest;
import com.netology.cloud.model.dto.LoginResponse;
import com.netology.cloud.security.AuthInterceptor;
import com.netology.cloud.service.AuthService;
import com.netology.cloud.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit-тесты контроллера авторизации (MockMvc).
 */
@WebMvcTest({AuthController.class, FileController.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private FileService fileService;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @Test
    void login_shouldReturnAuthTokenField() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(new LoginResponse("token123"));

        LoginRequest request = new LoginRequest();
        request.setLogin("test");
        request.setPassword("test");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['auth-token']").value("token123"));
    }
}

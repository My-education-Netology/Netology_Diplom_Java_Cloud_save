package com.netology.cloud.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты с Testcontainers (PostgreSQL).
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIf("com.netology.cloud.integration.PostgresTestSupport#isDockerAvailable")
class CloudServiceIntegrationTest {

    @BeforeAll
    static void startPostgres() {
        PostgresTestSupport.ensureStarted();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PostgresTestSupport.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", PostgresTestSupport.POSTGRES::getUsername);
        registry.add("spring.datasource.password", PostgresTestSupport.POSTGRES::getPassword);
        registry.add("storage.files.path", () -> "./target/test-storage");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullFileLifecycle_shouldWork() throws Exception {
        String token = loginAndGetToken();

        MockMultipartFile file = new MockMultipartFile(
                "file", "integration.txt", "text/plain", "hello".getBytes()
        );

        mockMvc.perform(multipart("/file")
                        .file(file)
                        .param("filename", "integration.txt")
                        .header("auth-token", "Bearer " + token))
                .andExpect(status().isOk());

        MvcResult listResult = mockMvc.perform(get("/list")
                        .param("limit", "10")
                        .header("auth-token", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode list = objectMapper.readTree(listResult.getResponse().getContentAsString());
        assertThat(list.isArray()).isTrue();
        assertThat(list.get(0).get("filename").asText()).isEqualTo("integration.txt");

        mockMvc.perform(get("/file")
                        .param("filename", "integration.txt")
                        .header("auth-token", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/file")
                        .param("filename", "integration.txt")
                        .header("auth-token", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void login_withWrongPassword_shouldReturn400() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"test\",\"password\":\"wrong\"}"))
                .andExpect(status().isBadRequest());
    }

    private String loginAndGetToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"test\",\"password\":\"test\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("auth-token").asText();
    }
}

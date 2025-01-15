package com.java.authservice.service;

import com.java.authservice.controller.dto.UserRequestDTO;
import com.java.authservice.controller.dto.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@PropertySource("classpath:/bootstrap-test.yml")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @Test
    public void saveUser() {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .username("username 1")
                .password("password 1")
                .build();
        UserResponseDTO userResponseDTO = authService.saveUser(userRequestDTO);

        assertNotNull(userResponseDTO);
        assertThat(userResponseDTO.getUsername()).isEqualTo(userRequestDTO.getUsername());

    }

    @Test
    public void generateToken() {
        String username = "username";
        String token = authService.generateToken(username);
        assertNotNull(token);
    }

    @Test
    public void validateToken() {
        String username = "username";
        String token = authService.generateToken(username);

        authService.validateToken(token);
    }
}
package com.java.authservice.repository;

import com.java.authservice.entity.User;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@PropertySource("classpath:/bootstrap-test.yml")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @BeforeEach
    public void prepare() {
        userRepository.deleteAll();
    }

    @Test
    public void findByUsername(){
        User user = User.builder()
                .username("username 1")
                .password("password 1")
                .build();
        userRepository.save(user);

        Optional<User> byUsername = userRepository.findByUsername(user.getUsername());
        assertTrue(byUsername.isPresent());
    }

    @Test
    public void saveBook(){
        User user = User.builder()
                .username("username 1")
                .password("password 1")
                .build();
        int sizeBeforeSave = userRepository.findAll().size();
        userRepository.save(user);
        int sizeAfterSave = userRepository.findAll().size();

        Optional<User> byUsername = userRepository.findByUsername(user.getUsername());
        assertTrue(byUsername.isPresent());
        assertThat(sizeAfterSave).isEqualTo(sizeBeforeSave + 1);
    }

}
//package com.java.bookservice.service;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Testcontainers
//@SpringBootTest
//class BookServiceTest {
//
//    @Container
//    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17-alpine");
//
//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//        registry.add("spring.jpa.generate-ddl", () -> true);
//    }
//
//    @Test
//    void createBook() {
//
//    }
//
//    @Test
//    void getAllBooks() {
//    }
//
//    @Test
//    void getAllFreeBook() {
//    }
//
//    @Test
//    void findBookById() {
//    }
//
//    @Test
//    void updateBook() {
//    }
//
//    @Test
//    void deleteBook() {
//    }
//}
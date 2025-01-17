package com.java.libraryservice.service;

import com.java.libraryservice.controller.dto.BookRecordResponseDTO;
import com.java.libraryservice.exception.BookRecordNotFoundException;
import com.java.libraryservice.exception.BookReturnedException;
import com.java.libraryservice.exception.BookTakenException;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@PropertySource("classpath:/bootstrap-test.yml")
class BookRecordServiceTest {

    @Autowired
    private BookRecordService bookRecordService;

    private final String isbn_1 = "1111111111111";
    private final String isbn_2 = "2222222222222";
    private final String isbn_3 = "3333333333333";
    private final Long id_1 = 1L;
    private String resetSequenceId = "ALTER SEQUENCE book_record_id_seq RESTART WITH 1";
    private int countBookRecordInit = 2;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        List<String> allIsbn = bookRecordService.findAll(0L, Long.MAX_VALUE).stream().map(BookRecordResponseDTO::getISBN).toList();
        allIsbn.forEach(isbn -> bookRecordService.deleteBookRecord(isbn));
        jdbcTemplate.execute(resetSequenceId);
        allIsbn.forEach(isbn -> bookRecordService.deleteBookRecord(isbn));

        bookRecordService.addBookRecord(isbn_1);
        bookRecordService.addBookRecord(isbn_2);
        countBookRecordInit = bookRecordService.findAll(0L, Long.MAX_VALUE).size();
    }

    @Test
    void findAll() {
        List<BookRecordResponseDTO> allBookRecordDTOs = bookRecordService.findAll(0L, Long.MAX_VALUE);

        assertNotNull(allBookRecordDTOs);
        assertThat(allBookRecordDTOs).hasSize(countBookRecordInit);

    }

    @Test
    void findAllFreeBook() {
        List<BookRecordResponseDTO> allBookRecordDTOs = bookRecordService.findAllFreeBook(0L, Long.MAX_VALUE);

        assertNotNull(allBookRecordDTOs);
        assertThat(allBookRecordDTOs).hasSize(countBookRecordInit);
    }

    @Test
    void findAllFreeBookIds() {
        List<String> allFreeBookIds = bookRecordService.findAllFreeBookIds(0L, Long.MAX_VALUE);

        assertNotNull(allFreeBookIds);
        assertThat(allFreeBookIds).hasSize(countBookRecordInit);
        assertThat(allFreeBookIds).containsExactly(isbn_1, isbn_2);
    }

    @Test
    void addBookRecord() {
        bookRecordService.addBookRecord(isbn_3);
        List<String> allFreeBookIdsAfterAddBook = bookRecordService.findAllFreeBookIds(0L, Long.MAX_VALUE);

        assertThat(allFreeBookIdsAfterAddBook.size() - 1).isEqualTo(countBookRecordInit);
        assertThat(allFreeBookIdsAfterAddBook).contains(isbn_3);

    }

    @Test
    void findBookRecordById() {
        BookRecordResponseDTO bookRecordDTOById = bookRecordService.findBookRecordById(id_1);

        assertThat(bookRecordDTOById.getISBN()).isEqualTo(isbn_1);
    }

    @Test
    void findBookRecordByISBN() {
        BookRecordResponseDTO bookRecordDTOByISBN = bookRecordService.findBookRecordByISBN(isbn_1);
        System.out.println(bookRecordDTOByISBN.getISBN());
        System.out.println(bookRecordDTOByISBN.getId());
        assertThat(bookRecordDTOByISBN.getId()).isEqualTo(id_1);
    }

    @Test
    void isNotTakenBook() {
        boolean isTakenBook = bookRecordService.isTakenBook(isbn_1);

        assertThat(isTakenBook).isFalse();
    }

    @Test
    void isTakenBook() {
        bookRecordService.takeBook(isbn_1);
        boolean isTakenBook = bookRecordService.isTakenBook(isbn_1);

        assertThat(isTakenBook).isTrue();
    }

    @Test
    void takeBook() {
        BookRecordResponseDTO dto = bookRecordService.takeBook(isbn_1);

        assertThat(dto).isNotNull();
        boolean takenBook = bookRecordService.isTakenBook(isbn_1);
        assertThat(takenBook).isTrue();
    }

    @Test
    void takeBook_whenBookIsTaken() {
        bookRecordService.takeBook(isbn_1);
        assertThrows(BookTakenException.class, () -> {
            BookRecordResponseDTO dto = bookRecordService.takeBook(isbn_1);
        });

        boolean takenBook = bookRecordService.isTakenBook(isbn_1);
        assertThat(takenBook).isTrue();
    }

    @Test
    void returnBook() {
        bookRecordService.takeBook(isbn_1);
        BookRecordResponseDTO dto = bookRecordService.returnBook(isbn_1);

        assertThat(dto).isNotNull();
        boolean takenBook = bookRecordService.isTakenBook(isbn_1);
        assertThat(takenBook).isFalse();
    }

    @Test
    void returnBook_whenBookIsNotTaken() {
        assertThrows(BookReturnedException.class, () -> {
            BookRecordResponseDTO dto = bookRecordService.returnBook(isbn_1);
        });

        boolean takenBook = bookRecordService.isTakenBook(isbn_1);
        assertThat(takenBook).isFalse();
    }

    @Test()
    void deleteBookRecord() {
        assertThrows(BookRecordNotFoundException.class, () -> {
            boolean isDeleted = bookRecordService.deleteBookRecord(isbn_1);
            BookRecordResponseDTO bookRecordDTOByISBN = bookRecordService.findBookRecordByISBN(isbn_1);

            assertThat(isDeleted).isTrue(); }
        );
    }
}
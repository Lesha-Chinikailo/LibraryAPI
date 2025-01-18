package com.java.bookservice.service;

import com.java.bookservice.controller.dto.BookRequestDTO;
import com.java.bookservice.controller.dto.BookResponseDTO;
import com.java.bookservice.models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@PropertySource("classpath:/bootstrap-test.yml")
class BookServiceTest {

    @Autowired
    private BookService bookService;
    private final String isbn_1 = "1111111111111";
    private final String isbn_2 = "2222222222222";
    private final String isbn_3 = "3333333333333";

    @Autowired
    private Properties configProperties;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

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
    void init() {
        List<String> listIsbn = bookService.getAllBooks(0L, Long.MAX_VALUE).stream().map(BookResponseDTO::getISBN).toList();
        preparationMockServiceResponseInitMethod(listIsbn);
        listIsbn.forEach(isbn -> bookService.deleteBook(isbn));

        BookRequestDTO bookRequestDTO_1 = BookRequestDTO.builder()
                .ISBN(isbn_1)
                .title("Book 1")
                .genre("genre 1")
                .description("description 1")
                .author("author 1")
                .build();
        BookRequestDTO bookRequestDTO_2 = BookRequestDTO.builder()
                .ISBN(isbn_2)
                .title("Book 2")
                .genre("genre 2")
                .description("description 2")
                .author("author 2")
                .build();
        bookService.createBook(bookRequestDTO_1);
        bookService.createBook(bookRequestDTO_2);
    }

    @Test
    void createBook() {
        preparationBeforeCreateMethod();

        BookRequestDTO bookRequestDTO = BookRequestDTO.builder()
                .ISBN(isbn_3)
                .title("Book 3")
                .genre("genre 3")
                .description("description 3")
                .author("author 3")
                .build();
        int sizeBeforeSave = bookService.getAllBooks(0L, Long.MAX_VALUE).size();
        bookService.createBook(bookRequestDTO);
        int sizeAfterSave = bookService.getAllBooks(0L, Long.MAX_VALUE).size();

        Optional<BookResponseDTO> dtoById = bookService.findBookById(isbn_3);
        assertTrue(dtoById.isPresent());
        assertThat(dtoById.get().getISBN()).isEqualTo(isbn_3);
        assertThat(sizeAfterSave).isEqualTo(sizeBeforeSave + 1);
    }

    @Test
    void getAllBooks() {
        List<BookResponseDTO> BookResponseDTOs = bookService.getAllBooks(0L, Long.MAX_VALUE);
        assertThat(BookResponseDTOs).isNotNull();
        assertThat(BookResponseDTOs.size()).isEqualTo(2);
    }

    @Test
    void findBookById() {
        Optional<BookResponseDTO> byId = bookService.findBookById(isbn_1);
        assertTrue(byId.isPresent());
        assertThat(byId.get().getISBN()).isEqualTo(isbn_1);
    }

    @Test
    void updateBook() {
        preparationBeforeUpdateMethod();

        BookRequestDTO bookRequestDTO = BookRequestDTO.builder()
                .ISBN(isbn_3)
                .title("Book 3")
                .genre("genre 3")
                .description("description 3")
                .author("author 3")
                .build();

        BookResponseDTO bookResponseDTO = bookService.updateBook(isbn_1, bookRequestDTO);
        Optional<BookResponseDTO> byId = bookService.findBookById(isbn_1);
        assertTrue(byId.isPresent());
        assertThat(byId.get().getISBN()).isEqualTo(isbn_1);
        assertThat(byId.get().getTitle()).isEqualTo(bookRequestDTO.getTitle());
        assertThat(byId.get().getDescription()).isEqualTo(bookRequestDTO.getDescription());
    }

    @Test
    void deleteBook() {
        preparationBeforeDeleteMethod(isbn_1);
        bookService.deleteBook(isbn_1);

        Optional<BookResponseDTO> byId = bookService.findBookById(isbn_1);
        assertTrue(byId.isEmpty());
    }


    private void preparationMockServiceResponseInitMethod(List<String> isbns){
        mockServer = MockRestServiceServer.createServer(restTemplate);
        for (int i = 0; i < isbns.size(); i++) {
            prepareMockServerResponseDeleteBook(isbns.get(i));
        }

        prepareMockServerResponseCreateBook();
        prepareMockServerResponseCreateBook();
    }

    private void preparationBeforeUpdateMethod(){
        mockServer = MockRestServiceServer.createServer(restTemplate);
        prepareMockServerResponseUpdateBook();
    }

    private void preparationBeforeCreateMethod(){
        mockServer = MockRestServiceServer.createServer(restTemplate);
        prepareMockServerResponseCreateBook();
    }

    private void preparationBeforeDeleteMethod(String isbn){
        mockServer = MockRestServiceServer.createServer(restTemplate);
        prepareMockServerResponseDeleteBook(isbn);
    }

    private void prepareMockServerResponseCreateBook(){
        String propertyHealthCheck = configProperties.getProperty("url.healthcheck");
        mockServer.expect(requestTo(propertyHealthCheck))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        String propertyCreateBook = configProperties.getProperty("url.createBook");
        mockServer.expect(requestTo(propertyCreateBook))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));
    }

    private void prepareMockServerResponseDeleteBook(String isbn){
        String propertyHealthCheck = configProperties.getProperty("url.healthcheck");
        mockServer.expect(requestTo(propertyHealthCheck))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        String propertyIsTaken = configProperties.getProperty("url.isTakenBook");
        mockServer.expect(requestTo(propertyIsTaken + isbn))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("false"));

        String propertyGet = configProperties.getProperty("url.getBookRecordByISBN");

        String propertyDelete = configProperties.getProperty("url.deleteBookRecord");
        mockServer.expect(requestTo(propertyDelete + isbn))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{}"));

        mockServer.expect(requestTo(propertyGet + isbn))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{}"));
    }

    private void prepareMockServerResponseUpdateBook() {
        String propertyHealthCheck = configProperties.getProperty("url.healthcheck");
        mockServer.expect(requestTo(propertyHealthCheck))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        String propertyIsTaken = configProperties.getProperty("url.isTakenBook");
        mockServer.expect(requestTo(propertyIsTaken + isbn_1))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("false"));
    }
}
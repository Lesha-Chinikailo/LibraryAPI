package com.java.bookservice.repository;

import com.java.bookservice.BookServiceApplication;
import com.java.bookservice.models.Book;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BookServiceApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;
    private final String isbn_1 = "1111111111111";
    private final String isbn_2 = "2222222222222";

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
        bookRepository.deleteAll();
        List<Book> books = List.of(
                Book.builder()
                        .ISBN(isbn_1)
                        .title("Book 1")
                        .genre("genre 1")
                        .description("description 1")
                        .author("author 1")
                        .build(),
                Book.builder()
                        .ISBN(isbn_2)
                        .title("Book 2")
                        .genre("genre 2")
                        .description("description 2")
                        .author("author 2")
                        .build()
        );
        bookRepository.saveAll(books);
    }

    @Test
    public void getBookById(){
        Optional<Book> byId = bookRepository.findById(isbn_1);
        assertTrue(byId.isPresent());
        assertThat(byId.get().getISBN()).isEqualTo(isbn_1);
    }

    @Test
    public void getAllBook(){
        List<Book> allBooks = bookRepository.findAll();
        assertThat(allBooks).isNotNull();
        assertThat(allBooks.size()).isEqualTo(2);
    }

    @Test
    public void saveBook(){
        String isbn = "3333333333333";
        Book book = Book.builder()
                .ISBN(isbn)
                .title("Book 3")
                .genre("genre 3")
                .description("description 3")
                .author("author 3")
                .build();
        int sizeBeforeSave = bookRepository.findAll().size();
        bookRepository.save(book);
        int sizeAfterSave = bookRepository.findAll().size();

        Optional<Book> byId = bookRepository.findById(isbn);
        assertTrue(byId.isPresent());
        assertThat(byId.get().getISBN()).isEqualTo(isbn);
        assertThat(sizeAfterSave).isEqualTo(sizeBeforeSave + 1);
    }

    @Test
    public void deleteBookById(){
        bookRepository.deleteById(isbn_1);

        Optional<Book> byId = bookRepository.findById(isbn_1);
        assertTrue(byId.isEmpty());
    }

    @Test
    public void deleteAllBook(){
        bookRepository.deleteAll();

        List<Book> allBooks = bookRepository.findAll();
        assertThat(allBooks.size()).isEqualTo(0);
    }

    public void existsBookById(){
        boolean exists = bookRepository.existsById(isbn_1);
        assertTrue(exists);
    }

}
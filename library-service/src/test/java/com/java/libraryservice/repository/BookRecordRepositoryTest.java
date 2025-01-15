//package com.java.libraryservice.repository;
//
//import com.java.libraryservice.LibraryServiceApplication;
//import com.java.libraryservice.models.BookRecord;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.transaction.annotation.Transactional;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Testcontainers
//@SpringBootTest
//@ActiveProfiles("test")
//@PropertySource("classpath:/bootstrap-test.yml")
//class BookRecordRepositoryTest {
//
//    @Autowired
//    private BookRecordRepository bookRecordRepository;
//    private final String isbn_1 = "1111111111111";
//    private final String isbn_2 = "2222222222222";
//    private final Long id_1 = 1L;
//    private final Long id_2 = 2L;
//    private String resetSequenceId = "ALTER SEQUENCE book_record_id_seq RESTART WITH 1";
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
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @BeforeEach
//    void init() {
//        bookRecordRepository.deleteAll();
//        jdbcTemplate.execute(resetSequenceId);
//        bookRecordRepository.deleteAll();
//
//        List<BookRecord> bookRecords = List.of(
//                BookRecord.builder()
//                        .ISBN(isbn_1)
//                        .dateTimeTakeOfBook(null)
//                        .dateTimeReturnOfBook(null)
//                        .build(),
//                BookRecord.builder()
//                        .ISBN(isbn_2)
//                        .dateTimeTakeOfBook(null)
//                        .dateTimeReturnOfBook(null)
//                        .build()
//        );
//        bookRecordRepository.saveAll(bookRecords);
//    }
//
//    @Test
//    public void getBookRecordById(){
//        Optional<BookRecord> byId = bookRecordRepository.findById(id_1);
//        assertTrue(byId.isPresent());
//        assertThat(byId.get().getId()).isEqualTo(id_1);
//    }
//
//    @Test
//    public void getAllBookRecords(){
//        List<BookRecord> allBooks = bookRecordRepository.findAll();
//        assertThat(allBooks).isNotNull();
//        assertThat(allBooks.size()).isEqualTo(2);
//    }
//
//    @Test
//    public void getBookRecordByIsbn(){
//        Optional<BookRecord> byISBN = bookRecordRepository.findByISBN(isbn_1);
//        assertTrue(byISBN.isPresent());
//        assertThat(byISBN.get().getISBN()).isEqualTo(isbn_1);
//    }
//
//    @Test
//    public void saveBook(){
//        String isbn = "3333333333333";
//        BookRecord bookRecord = BookRecord.builder()
//                .ISBN(isbn)
//                .dateTimeTakeOfBook(null)
//                .dateTimeReturnOfBook(null)
//                .build();
//        int sizeBeforeSave = bookRecordRepository.findAll().size();
//        bookRecordRepository.save(bookRecord);
//        int sizeAfterSave = bookRecordRepository.findAll().size();
//
//        Optional<BookRecord> byISBN = bookRecordRepository.findByISBN(isbn);
//        assertTrue(byISBN.isPresent());
//        assertThat(byISBN.get().getISBN()).isEqualTo(isbn);
//        assertThat(sizeAfterSave).isEqualTo(sizeBeforeSave + 1);
//    }
//
//    @Test
//    public void deleteBookById(){
//        bookRecordRepository.deleteById(id_1);
//
//        Optional<BookRecord> byId = bookRecordRepository.findById(id_1);
//        assertTrue(byId.isEmpty());
//    }
//
//    @Test
//    @Transactional
//    public void deleteBookByIsbn(){
//        bookRecordRepository.deleteByISBN(isbn_1);
//
//        Optional<BookRecord> byISBN = bookRecordRepository.findByISBN(isbn_1);
//        assertTrue(byISBN.isEmpty());
//    }
//
//    @Test
//    public void deleteAllBook(){
//        bookRecordRepository.deleteAll();
//
//        List<BookRecord> allBooks = bookRecordRepository.findAll();
//        assertThat(allBooks.size()).isEqualTo(0);
//    }
//
//    @Test
//    public void existsBookById(){
//        boolean exists = bookRecordRepository.existsById(id_1);
//        assertTrue(exists);
//    }
//}
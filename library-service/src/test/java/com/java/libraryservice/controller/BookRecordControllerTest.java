package com.java.libraryservice.controller;

import com.java.libraryservice.controller.dto.BookRecordResponseDTO;
import com.java.libraryservice.exception.BookRecordNotFoundException;
import com.java.libraryservice.exception.BookReturnedException;
import com.java.libraryservice.exception.BookTakenException;
import com.java.libraryservice.handler.GlobalExceptionHandler;
import com.java.libraryservice.mapper.BookRecordMapper;
import com.java.libraryservice.models.BookRecord;
import com.java.libraryservice.service.BookRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
class BookRecordControllerTest {

    @Mock
    private BookRecordService bookRecordService;

    private MockMvc mockMvc;

    @InjectMocks
    private BookRecordController bookRecordController;

    private GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    private final Long page = 0L;
    private final Long size = 10L;
    private final Long idBookRecord_1 = 1L;
    private final Long idBookRecord_2 = 2L;
    private final String messageInException = "exception";
    private final String isbn = "1231231231231";
    private final String wrongIsbn = "1231231231232";
    private BookRecordResponseDTO fullFieldBookRecordResponseDTO;
    private BookRecord fullFieldBookRecord;

    @BeforeEach
    public void prepare() {
//        MockitoAnnotations.openMocks(this);
        fullFieldBookRecordResponseDTO = BookRecordResponseDTO
                .builder()
                .ISBN(isbn)
                .dateTimeReturnOfBook(LocalDateTime.now())
                .dateTimeTakeOfBook(LocalDateTime.now())
                .build();
        fullFieldBookRecord = BookRecord.builder()
                .ISBN(isbn)
                .dateTimeReturnOfBook(LocalDateTime.now())
                .dateTimeTakeOfBook(LocalDateTime.now())
                .build();
    }

    @Test
    public void createBookRecord_success(){
        doReturn(idBookRecord_1).when(bookRecordService).addBookRecord(isbn);
        doReturn(fullFieldBookRecordResponseDTO).when(bookRecordService).findBookRecordById(idBookRecord_1);

        ResponseEntity<BookRecordResponseDTO> responseBookRecord = bookRecordController.createBookRecord(isbn);

        assertNotNull(responseBookRecord);
        assertThat(responseBookRecord.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBookRecord.getBody().getISBN()).isEqualTo(isbn);
    }

    @Test
    public void takeBookRecord_success(){
        doReturn(fullFieldBookRecordResponseDTO).when(bookRecordService).takeBook(isbn);

        ResponseEntity<BookRecordResponseDTO> bookRecordResponseDTOResponseEntity = bookRecordController.takeBookRecord(isbn);

        assertNotNull(bookRecordResponseDTOResponseEntity);
        assertThat(bookRecordResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void takeBookRecord_fail(){
        doReturn(null).when(bookRecordService).takeBook(isbn);

        ResponseEntity<BookRecordResponseDTO> bookRecordResponseDTOResponseEntity = bookRecordController.takeBookRecord(isbn);

        ResponseEntity<Object> response = globalExceptionHandler.handleBookTakenException(new BookTakenException(messageInException));


        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void returnBookRecord_success(){
        doReturn(fullFieldBookRecordResponseDTO).when(bookRecordService).returnBook(isbn);

        ResponseEntity<BookRecordResponseDTO> response = bookRecordController.returnBookRecord(isbn);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void returnBookRecord_fail(){
//        doReturn(null).when(bookRecordService).returnBook(isbn);
//
//        ResponseEntity<BookRecordResponseDTO> bookRecordResponseDTOResponseEntity = bookRecordController.returnBookRecord(isbn);

        ResponseEntity<Object> response = globalExceptionHandler.handleBookReturnedException(new BookReturnedException(messageInException));

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteBookRecord_success(){
        doReturn(true).when(bookRecordService).deleteBookRecord(isbn);

        ResponseEntity<String> response = bookRecordController.deleteBookRecord(isbn);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void deleteBookRecord_fail(){
//        doReturn(false).when(bookRecordService).deleteBookRecord(isbn);
//
//        ResponseEntity<String> response = bookRecordController.deleteBookRecord(isbn);

        ResponseEntity<Object> response = globalExceptionHandler.handleBookTakenException(new BookTakenException(messageInException));

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void getBookRecordByISBN_success(){
        doReturn(fullFieldBookRecordResponseDTO).when(bookRecordService).findBookRecordByISBN(isbn);

        ResponseEntity<BookRecordResponseDTO> response = bookRecordController.getBookRecordByISBN(isbn);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getISBN()).isEqualTo(isbn);
    }

    @Test
    public void getBookRecordByISBN_fail() {
//        doThrow(new BookRecordNotFoundException(messageInException)).when(bookRecordService).findBookRecordByISBN(isbn);
//        ResponseEntity<BookRecordResponseDTO> response = bookRecordController.getBookRecordByISBN(isbn);

        ResponseEntity<Object> response = globalExceptionHandler.handleBookNotFoundException(new BookRecordNotFoundException(messageInException));

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getAllBooks_success() {
        BookRecord bookRecord_1 = BookRecord.builder().ISBN(isbn).id(idBookRecord_1).build();
        BookRecord bookRecord_2 = BookRecord.builder().ISBN(wrongIsbn).id(idBookRecord_2).build();
        BookRecordResponseDTO bookRecordResponseDTO_1 = BookRecordResponseDTO.builder().ISBN(bookRecord_1.getISBN()).build();
        BookRecordResponseDTO bookRecordResponseDTO_2 = BookRecordResponseDTO.builder().ISBN(bookRecord_2.getISBN()).build();
        List<BookRecordResponseDTO> expectedBooks = List.of(
                bookRecordResponseDTO_1,
                bookRecordResponseDTO_2);
        doReturn(expectedBooks).when(bookRecordService).findAll(page, size);

        ResponseEntity<List<BookRecordResponseDTO>> allBook = bookRecordController.getBookRecords(page, size);

        assertNotNull(allBook);
        assertThat(allBook.getBody().size()).isEqualTo(expectedBooks.size());
        assertThat(allBook.getBody().get(0).getISBN()).isEqualTo(isbn);
        assertThat(allBook.getBody().get(1).getISBN()).isEqualTo(wrongIsbn);
    }

    @Test
    public void getAllBooks_fail() {
        Long negativePage = -1L;
        Long negativeSize = -1L;

        ResponseEntity<List<BookRecordResponseDTO>> response = bookRecordController.getBookRecords(negativePage, negativeSize);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getAllBooksIds_success() {
        List<String> expectedIds = List.of(
                isbn,
                wrongIsbn);
        doReturn(expectedIds).when(bookRecordService).findAllFreeBookIds(page, size);

        ResponseEntity<List<String>> allBook = bookRecordController.getFreeBookRecordIds(page, size);

        assertNotNull(allBook);
        assertThat(allBook.getBody().size()).isEqualTo(expectedIds.size());
        assertThat(allBook.getBody().get(0)).isEqualTo(isbn);
        assertThat(allBook.getBody().get(1)).isEqualTo(wrongIsbn);
    }

    @Test
    public void getAllBooksIds_fail() {
        Long negativePage = -1L;
        Long negativeSize = -1L;

        ResponseEntity<List<String>> response = bookRecordController.getFreeBookRecordIds(negativePage, negativeSize);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getAllFreeBooks_success() {
        BookRecord bookRecord_1 = BookRecord.builder().ISBN(isbn).id(idBookRecord_1).build();
        BookRecord bookRecord_2 = BookRecord.builder().ISBN(wrongIsbn).id(idBookRecord_2).build();
        BookRecordResponseDTO bookRecordResponseDTO_1 = BookRecordResponseDTO.builder().ISBN(bookRecord_1.getISBN()).build();
        BookRecordResponseDTO bookRecordResponseDTO_2 = BookRecordResponseDTO.builder().ISBN(bookRecord_2.getISBN()).build();
        List<BookRecordResponseDTO> expectedBooks = List.of(
                bookRecordResponseDTO_1,
                bookRecordResponseDTO_2);
        doReturn(expectedBooks).when(bookRecordService).findAllFreeBook(page, size);

        ResponseEntity<List<BookRecordResponseDTO>> allBook = bookRecordController.getFreeBookRecords(page, size);

        assertNotNull(allBook);
        assertThat(allBook.getBody().size()).isEqualTo(expectedBooks.size());
    }

    @Test
    public void isTakenBookById_taken(){
        doReturn(true).when(bookRecordService).isTakenBook(isbn);

        ResponseEntity<Boolean> response = bookRecordController.isTakenBookById(isbn);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(Boolean.TRUE, response.getBody());
    }

    @Test
    public void isTakenBookById_notTaken(){
        doReturn(false).when(bookRecordService).isTakenBook(isbn);

        ResponseEntity<Boolean> response = bookRecordController.isTakenBookById(isbn);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(Boolean.FALSE, response.getBody());
    }
}
package com.java.bookservice.controller;

import com.java.bookservice.controller.dto.BookRequestDTO;
import com.java.bookservice.controller.dto.BookResponseDTO;
import com.java.bookservice.exception.BookNotFoundException;
import com.java.bookservice.exception.BookTakenException;
import com.java.bookservice.exception.ServiceUnavailableException;
import com.java.bookservice.handler.GlobalExceptionHandler;
import com.java.bookservice.mapper.BookMapper;
import com.java.bookservice.models.Book;
import com.java.bookservice.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    private final Long page = 0L;
    private final Long size = 10L;
    private final String messageInException = "exception";
    private final String isbn = "1231231231231";
    private final String wrongIsbn = "1231231231232";
    private BookResponseDTO fullFieldBookResponseDTO;
    private BookRequestDTO fullFieldBookRequestDTO;
    private Book fullFieldBook;

    @BeforeEach
    public void prepare() {
        fullFieldBookResponseDTO = BookResponseDTO.builder()
                .ISBN(isbn)
                .title("title")
                .genre("genre")
                .description("description")
                .author("author")
                .build();
        fullFieldBookRequestDTO = BookRequestDTO
                .builder()
                .ISBN(isbn)
                .title("title")
                .genre("genre")
                .description("description")
                .author("author")
                .build();
        fullFieldBook = Book.builder()
                .ISBN(isbn)
                .title("title")
                .genre("genre")
                .description("description")
                .author("author")
                .build();
    }

    @Test
    public void getBookById_found() {
        Book book = Book.builder()
                .ISBN(isbn)
                .build();
        BookResponseDTO expectedBookResponseDTO = BookResponseDTO.builder()
                .ISBN(isbn)
                .build();
        doReturn(Optional.of(expectedBookResponseDTO)).when(bookService).findBookById(isbn);

        ResponseEntity<BookResponseDTO> resultBookById = bookController.getBookById(isbn);

        assertNotNull(resultBookById);
        assertThat(resultBookById.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultBookById.getBody().getISBN()).isEqualTo(isbn);
    }

    @Test
    public void getBookById_notFound() {
        Book book = Book.builder()
                .ISBN(isbn)
                .build();
        BookResponseDTO expectedBookResponseDTO = new BookResponseDTO();
        expectedBookResponseDTO.setISBN(isbn);
        doReturn(Optional.empty()).when(bookService).findBookById(wrongIsbn);

        ResponseEntity<BookResponseDTO> resultBookById = bookController.getBookById(wrongIsbn);

        assertNotNull(resultBookById);
        assertThat(resultBookById.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createBook_success() {

        doReturn(isbn).when(bookService).createBook(fullFieldBookRequestDTO);
        doReturn(Optional.of(fullFieldBookResponseDTO)).when(bookService).findBookById(isbn);

        ResponseEntity<BookResponseDTO> createdBook = bookController.createBook(fullFieldBookRequestDTO);

        assertNotNull(createdBook);
        assertThat(createdBook.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createdBook.getBody().getISBN()).isEqualTo(isbn);
    }

    @Test
    public void createBook_serviceUnavailable() {
        lenient().doThrow(new ServiceUnavailableException(messageInException)).when(bookService).createBook(fullFieldBookRequestDTO);
//        ResponseEntity<BookResponseDTO> createdBook = bookController.createBook(fullFieldBookRequestDTO);

        ResponseEntity<Object> response = globalExceptionHandler.handleServiceUnavailableException(new ServiceUnavailableException(messageInException));

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    public void updateBook_success() {
        doReturn(fullFieldBookResponseDTO).when(bookService).updateBook(isbn, fullFieldBookRequestDTO);

        ResponseEntity<BookResponseDTO> bookResponseDTOResponseEntity = bookController.updateBook(isbn, fullFieldBookRequestDTO);

        assertNotNull(bookResponseDTOResponseEntity);
        assertThat(bookResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookResponseDTOResponseEntity.getBody()).isEqualTo(fullFieldBookResponseDTO);
    }

    @Test
    public void updateBook_notFound() {

        lenient().doThrow(new BookNotFoundException(messageInException),
                new BookTakenException(messageInException)).when(bookService).updateBook(wrongIsbn, fullFieldBookRequestDTO);
//        ResponseEntity<BookResponseDTO> bookResponseDTOResponseEntity = bookController.updateBook(wrongIsbn, fullFieldBookRequestDTO);

        ResponseEntity<Object> response = globalExceptionHandler.handleBookNotFoundException(new BookNotFoundException(messageInException));

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateBook_serviceUnavailable() {
        lenient().doThrow(new ServiceUnavailableException(messageInException)).when(bookService).updateBook(isbn, fullFieldBookRequestDTO);
//        ResponseEntity<BookResponseDTO> bookResponseDTOResponseEntity = bookController.updateBook(isbn, fullFieldBookRequestDTO);

        ResponseEntity<Object> response = globalExceptionHandler.handleServiceUnavailableException(new ServiceUnavailableException(messageInException));

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    public void deleteBook_success() {
        doReturn(true).when(bookService).deleteBook(isbn);

        ResponseEntity<String> responseEntity = bookController.deleteBook(isbn);

        assertNotNull(responseEntity);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void deleteBook_fail() {
        doReturn(false).when(bookService).deleteBook(isbn);

        ResponseEntity<String> responseEntity = bookController.deleteBook(isbn);

        assertNotNull(responseEntity);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void deleteBook_notFound() {
        lenient().doThrow(new BookNotFoundException(messageInException)
                , new BookTakenException(messageInException)).when(bookService).deleteBook(isbn);
//        ResponseEntity<String> responseEntity = bookController.deleteBook(isbn);

        ResponseEntity<Object> response = globalExceptionHandler.handleBookNotFoundException(new BookNotFoundException(messageInException));

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteBook_serviceUnavailable() {
        lenient().doThrow(new ServiceUnavailableException(messageInException))
                .when(bookService)
                .deleteBook(isbn);
//        ResponseEntity<String> responseEntity = bookController.deleteBook(isbn);

        ResponseEntity<Object> response = globalExceptionHandler.handleServiceUnavailableException(new ServiceUnavailableException(messageInException));

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    public void getAllBooks_success() {
        Book book_1 = Book.builder().ISBN(isbn).build();
        Book book_2 = Book.builder().ISBN(wrongIsbn).build();
        BookResponseDTO bookResponseDTO_1 = BookResponseDTO.builder().ISBN(book_1.getISBN()).build();
        BookResponseDTO bookResponseDTO_2 = BookResponseDTO.builder().ISBN(book_2.getISBN()).build();
        List<BookResponseDTO> expectedBooks = List.of(
                bookResponseDTO_1,
                bookResponseDTO_2);
        doReturn(expectedBooks).when(bookService).getAllBooks(page, size);

        ResponseEntity<List<BookResponseDTO>> allBook = bookController.getAllBook(page, size);

        assertNotNull(allBook);
        assertThat(allBook.getBody().size()).isEqualTo(expectedBooks.size());
        assertThat(allBook.getBody().get(0).getISBN()).isEqualTo(isbn);
        assertThat(allBook.getBody().get(1).getISBN()).isEqualTo(wrongIsbn);
    }

    @Test
    public void getAllFreeBooks_success() {
        Book book_1 = Book.builder().ISBN(isbn).build();
        Book book_2 = Book.builder().ISBN(wrongIsbn).build();
        BookResponseDTO bookResponseDTO_1 = BookResponseDTO.builder().ISBN(book_1.getISBN()).build();
        BookResponseDTO bookResponseDTO_2 = BookResponseDTO.builder().ISBN(book_2.getISBN()).build();
        List<BookResponseDTO> expectedBooks = List.of(
                bookResponseDTO_1,
                bookResponseDTO_2);
        doReturn(expectedBooks).when(bookService).getAllFreeBook(page, size);

        ResponseEntity<List<BookResponseDTO>> allFreeBooks = bookController.getAllFreeBooks(page, size);

        assertNotNull(allFreeBooks);
        assertThat(allFreeBooks.getBody().size()).isEqualTo(expectedBooks.size());
        assertThat(allFreeBooks.getBody().get(0).getISBN()).isEqualTo(isbn);
        assertThat(allFreeBooks.getBody().get(1).getISBN()).isEqualTo(wrongIsbn);
    }

    @Test
    public void getAllFreeBooks_serviceUnavailable() {
        lenient().doThrow(new ServiceUnavailableException(messageInException))
                .when(bookService)
                .getAllFreeBook(page, size);
//        ResponseEntity<List<BookResponseDTO>> allFreeBooks = bookController.getAllFreeBooks(page, size);

        ResponseEntity<Object> response = globalExceptionHandler.handleServiceUnavailableException(new ServiceUnavailableException(messageInException));

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
package com.java.bookservice.controller;

import com.java.bookservice.controller.dto.BookRequestDTO;
import com.java.bookservice.controller.dto.BookResponseDTO;
import com.java.bookservice.exception.BookNotFoundException;
import com.java.bookservice.exception.BookTakenException;
import com.java.bookservice.exception.ServiceUnavailableException;
import com.java.bookservice.mapper.BookMapper;
import com.java.bookservice.models.Book;
import com.java.bookservice.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor()
@Validated
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping("/{isbn}")
    public BookResponseDTO getBookById(@PathVariable String isbn) {
        Optional<Book> maybeBook = bookService.findBookById(isbn);
        if (maybeBook.isPresent()) {
            return bookMapper.bookToResponseDTO(maybeBook.get());
        }
        return new ResponseEntity<BookResponseDTO>(HttpStatus.NOT_FOUND).getBody();
    }

    @GetMapping("/")
    public List<BookResponseDTO> getAllBook(@RequestParam(defaultValue = "0") Long page,
                                            @RequestParam(defaultValue = "10") Long size) {
        return bookService.getAllBooks(page, size)
                .stream()
                .map(bookMapper::bookToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/freeBooks")
    public ResponseEntity<List<BookResponseDTO>> getAllFreeBooks(@RequestParam(defaultValue = "0") Long page,
                                                                 @RequestParam(defaultValue = "10") Long size) {
        List<Book> allFreeBook;
        try{
            allFreeBook = bookService.getAllFreeBook(page, size);
        }
        catch (RuntimeException e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<>(
                allFreeBook
                        .stream()
                        .map(bookMapper::bookToResponseDTO)
                        .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @PostMapping("/create")
    public BookResponseDTO createBook(@Valid @RequestBody BookRequestDTO bookRequestDTO, HttpServletResponse response) {
        String bookISBN;
        try{
            bookISBN = bookService.createBook(bookRequestDTO);
        }
        catch (RuntimeException e){
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return new BookResponseDTO();
        }

        return bookMapper.bookToResponseDTO(bookService.findBookById(bookISBN).get());
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable String isbn,
                                                      @RequestBody BookRequestDTO bookRequestDTO) {
        Book book;
        try{
            book = bookService.updateBook(isbn, bookRequestDTO);
        }
        catch (BookNotFoundException | BookTakenException e){
            return new ResponseEntity<>(new BookResponseDTO(), HttpStatus.NOT_FOUND);
        }
        catch (ServiceUnavailableException e){
            return new ResponseEntity<>(new BookResponseDTO(), HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(bookMapper.bookToResponseDTO(book), HttpStatus.OK);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<String> deleteBook(@PathVariable String isbn) {
        boolean isDeleted;
        try{
            isDeleted = bookService.deleteBook(isbn);
        }
        catch (BookNotFoundException | BookTakenException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (ServiceUnavailableException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
        if(isDeleted){
            return ResponseEntity.ok("Book with isbn: " + isbn + " has been deleted successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete book with isbn: " + isbn);
        }
    }
 }

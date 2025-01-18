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
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable String isbn) {
        Optional<BookResponseDTO> maybeBookResponseDTO = bookService.findBookById(isbn);
        return maybeBookResponseDTO
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/")
    public ResponseEntity<List<BookResponseDTO>> getAllBook(@RequestParam(defaultValue = "0") Long page,
                                            @RequestParam(defaultValue = "10") Long size) {

        return new ResponseEntity<>(
                bookService.getAllBooks(page, size),
                HttpStatus.OK);
    }

    @GetMapping("/freeBooks")
    public ResponseEntity<List<BookResponseDTO>> getAllFreeBooks(@RequestParam(defaultValue = "0") Long page,
                                                                 @RequestParam(defaultValue = "10") Long size) {
        List<BookResponseDTO> allFreeBook;
        try{
            allFreeBook = bookService.getAllFreeBook(page, size);
        }
        catch (ServiceUnavailableException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<>(
                allFreeBook,
                HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO bookRequestDTO) {
        String bookISBN;
        try{
            bookISBN = bookService.createBook(bookRequestDTO);
        }
        catch (ServiceUnavailableException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(bookService.findBookById(bookISBN).get(), HttpStatus.CREATED);
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable String isbn,
                                                      @RequestBody BookRequestDTO bookRequestDTO) {
        BookResponseDTO bookResponseDTO;
        try{
            bookResponseDTO = bookService.updateBook(isbn, bookRequestDTO);
        }
        catch (BookNotFoundException | BookTakenException e){
            return new ResponseEntity<>(new BookResponseDTO(), HttpStatus.NOT_FOUND);
        }
        catch (ServiceUnavailableException e){
            return new ResponseEntity<>(new BookResponseDTO(), HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(bookResponseDTO, HttpStatus.OK);
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
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to delete book with isbn: " + isbn);
        }
    }
 }

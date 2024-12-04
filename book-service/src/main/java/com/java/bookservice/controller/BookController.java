package com.java.bookservice.controller;

import com.java.bookservice.controller.dto.BookRequestDTO;
import com.java.bookservice.controller.dto.BookResponseDTO;
import com.java.bookservice.mapper.BookMapper;
import com.java.bookservice.models.Book;
import com.java.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor()
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping("/{id}")
    public BookResponseDTO getBookById(@PathVariable Long id) {
        Optional<Book> maybeBook = bookService.findBookById(id);
        if (maybeBook.isPresent()) {
            return bookMapper.bookToResponseDTO(maybeBook.get());
        }
        return new ResponseEntity<BookResponseDTO>(HttpStatus.NOT_FOUND).getBody();
    }

    @GetMapping("/isbn/{isbn}")
    public BookResponseDTO getBookByISBN(@PathVariable String isbn) {
        Book book = bookService.findBookByISBN(isbn);
        return bookMapper.bookToResponseDTO(book);
        //return new ResponseEntity<BookResponseDTO>(HttpStatus.NOT_FOUND).getBody();
    }

    @GetMapping("/")
    public List<BookResponseDTO> getAllBook() {
        return bookService.getAllBooks()
                .stream()
                .map(bookMapper::bookToResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/")
    public Long createBook(@RequestBody BookRequestDTO bookRequestDTO) {
        return bookService.createBook(bookRequestDTO);
    }

    @PutMapping("/{id}")
    public BookResponseDTO updateBook(@PathVariable Long id,
                                      @RequestBody BookRequestDTO bookRequestDTO) {
        return bookMapper.bookToResponseDTO(bookService.updateBook(id, bookRequestDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}

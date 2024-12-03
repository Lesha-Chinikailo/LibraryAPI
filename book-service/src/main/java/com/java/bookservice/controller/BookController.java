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

import java.util.Optional;

@RestController
@RequiredArgsConstructor()
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping("/{id}")
    public BookResponseDTO getBookById(@PathVariable Long id) {
        Optional<Book> maybeBook = bookService.findBookById(id);
        if (maybeBook.isPresent()) {
            Book book = maybeBook.get();
            BookResponseDTO responseDTO = bookMapper.bookToResponseDTO(book);
            return responseDTO;
        }
        return new ResponseEntity<BookResponseDTO>(HttpStatus.NOT_FOUND).getBody();
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

    }
}

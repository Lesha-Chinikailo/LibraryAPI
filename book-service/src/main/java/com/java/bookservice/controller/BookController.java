package com.java.bookservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.bookservice.controller.dto.BookRequestDTO;
import com.java.bookservice.controller.dto.BookResponseDTO;
import com.java.bookservice.mapper.BookMapper;
import com.java.bookservice.models.Book;
import com.java.bookservice.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor()
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;
    private final RestTemplate restTemplate;

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
 }

    @GetMapping("/")
    public List<BookResponseDTO> getAllBook() {
        return bookService.getAllBooks()
                .stream()
                .map(bookMapper::bookToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/freeBooks")
    public List<BookResponseDTO> getAllFreeBooks() {
        String url = "http://localhost:8082/records/free/ids"; // URL второго сервиса

        var response = restTemplate.getForEntity(url, Long[].class);
        var body = response.getBody();
        List<Long> list = body != null
                ? Arrays.stream(body).toList()
                : new ArrayList<>();

        List<Book> allFreeBook = bookService.getAllFreeBook(list);
        return allFreeBook
                .stream()
                .map(bookMapper::bookToResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/")
    public Long createBook(@RequestBody BookRequestDTO bookRequestDTO, HttpServletResponse response) {
        Long bookId = bookService.createBook(bookRequestDTO);
        if(bookId == -1){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        String url = "http://localhost:8082/records/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Long> requestEntity = new HttpEntity<>(bookId, headers);

        ResponseEntity<String> responseFromService = restTemplate.postForEntity(url, requestEntity, String.class);
        return bookId;
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

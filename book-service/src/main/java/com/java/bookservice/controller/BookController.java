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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor()
//@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;
    private final RestTemplate restTemplate;
    private final Properties configProperties;

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
    public List<BookResponseDTO> getAllBook(@RequestParam(defaultValue = "0") Long page,
                                            @RequestParam(defaultValue = "10") Long size) {
        return bookService.getAllBooks(page, size)
                .stream()
                .map(bookMapper::bookToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/freeBooks")
    public List<BookResponseDTO> getAllFreeBooks(@RequestParam(defaultValue = "0") Long page,
                                                 @RequestParam(defaultValue = "10") Long size) {
        String url = configProperties.getProperty("url.getFreeBooks");

        var response = restTemplate.getForEntity(url, Long[].class);
        var body = response.getBody();
        List<Long> list = body != null
                ? Arrays.stream(body).toList()
                : new ArrayList<>();

        List<Book> allFreeBook = bookService.getAllFreeBook(list, page, size);
        return allFreeBook
                .stream()
                .map(bookMapper::bookToResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/create")
    public BookResponseDTO createBook(@RequestBody BookRequestDTO bookRequestDTO, HttpServletResponse response) {
        Long bookId = bookService.createBook(bookRequestDTO);
        if(bookId == -1){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        else {
            String url = configProperties.getProperty("url.createBook");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<Long> requestEntity = new HttpEntity<>(bookId, headers);

            restTemplate.postForEntity(url, requestEntity, String.class);
        }

        return bookId != -1
                ? bookMapper.bookToResponseDTO(bookService.findBookById(bookId).get())
                : new BookResponseDTO();
    }

    @PutMapping("/{id}")
    public BookResponseDTO updateBook(@PathVariable Long id,
                                      @RequestBody BookRequestDTO bookRequestDTO) {
        return bookMapper.bookToResponseDTO(bookService.updateBook(id, bookRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        boolean isDeleted = bookService.deleteBook(id);
        if(isDeleted){
            return ResponseEntity.ok("Book with Id " + id + " has been deleted successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete book with Id   " + id);
        }
    }
}

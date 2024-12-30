package com.java.bookservice.controller;

import com.java.bookservice.controller.dto.BookRequestDTO;
import com.java.bookservice.controller.dto.BookResponseDTO;
import com.java.bookservice.mapper.BookMapper;
import com.java.bookservice.models.Book;
import com.java.bookservice.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    public List<BookResponseDTO> getAllFreeBooks(@RequestParam(defaultValue = "0") Long page,
                                                 @RequestParam(defaultValue = "10") Long size) {
        String url = configProperties.getProperty("url.getFreeBooks");

        var response = restTemplate.getForEntity(url, String[].class);
        var body = response.getBody();
        List<String> list = body != null
                ? Arrays.stream(body).toList()
                : new ArrayList<>();

        List<Book> allFreeBook = bookService.getAllFreeBook(list, page, size);
        return allFreeBook
                .stream()
                .map(bookMapper::bookToResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/create")
    public BookResponseDTO createBook(@RequestBody @Valid BookRequestDTO bookRequestDTO, HttpServletResponse response) {
        String bookISBN = bookService.createBook(bookRequestDTO);
        String url = configProperties.getProperty("url.createBook");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(bookISBN, headers);

        restTemplate.postForEntity(url, requestEntity, String.class);

        return bookISBN != null
                ? bookMapper.bookToResponseDTO(bookService.findBookById(bookISBN).get())
                : new BookResponseDTO();
    }

    @PutMapping("/{isbn}")
    public BookResponseDTO updateBook(@PathVariable String isbn,
                                      @RequestBody BookRequestDTO bookRequestDTO) {
        return bookMapper.bookToResponseDTO(bookService.updateBook(isbn, bookRequestDTO));
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<String> deleteBook(@PathVariable String isbn) {
        boolean isDeleted = bookService.deleteBook(isbn);
        if(isDeleted){
            return ResponseEntity.ok("Book with isbn " + isbn + " has been deleted successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete book with isbn   " + isbn);
        }
    }
}

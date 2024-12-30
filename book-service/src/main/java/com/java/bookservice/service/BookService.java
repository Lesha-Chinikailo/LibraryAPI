package com.java.bookservice.service;

import com.java.bookservice.controller.dto.BookRequestDTO;
import com.java.bookservice.exception.BookNotFoundException;
import com.java.bookservice.mapper.BookMapper;
import com.java.bookservice.models.Book;
import com.java.bookservice.repository.BookRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor()
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final RestTemplate restTemplate;
    private final Properties configProperties;

    public String createBook(BookRequestDTO dto){
        Book book = bookMapper.RequestDTOToBook(dto);

        if(!isServiceAvailable()){
            throw new RuntimeException("Sorry, library is not available. Try again later");
        }

        Book bookSaved = bookRepository.save(book);

        String url = configProperties.getProperty("url.createBook");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(bookSaved.getISBN(), headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        return bookSaved.getISBN();
    }

    private boolean isServiceAvailable() {
        String urlHealthCheck = configProperties.getProperty("url.healthcheck");

        ResponseEntity<String[]> responseEntity = restTemplate.getForEntity(urlHealthCheck, String[].class);

        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    public List<Book> getAllBooks(Long pageNumber, Long pageSize){
        return bookRepository.findAll()
                .stream()
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public List<Book> getAllFreeBook(Long pageNumber, Long pageSize){
        if(!isServiceAvailable()){
            throw new RuntimeException("Sorry, library is not available. Try again later");
        }

        String url = configProperties.getProperty("url.getFreeBooks");

        var response = restTemplate.getForEntity(url, String[].class);
        var body = response.getBody();
        List<String> list = body != null
                ? Arrays.stream(body).toList()
                : new ArrayList<>();

        return bookRepository.findAllById(list)
                .stream()
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public Optional<Book> findBookById(String isbn){
        Optional<Book> byId = bookRepository.findById(isbn);
        return Optional.ofNullable(byId
                .orElseThrow(() -> new BookNotFoundException("Unable to find book with isbn: " + isbn)));
    }

    public Book updateBook(String isbn, BookRequestDTO dto){
        if(findBookById(isbn).isEmpty()){
            return null;
        }
        Book book = findBookById(isbn).get();
        book.setISBN(dto.getISBN());
        book.setTitle(dto.getTitle());
        book.setGenre(dto.getGenre());
        book.setDescription(dto.getDescription());
        book.setAuthor(dto.getAuthor());
        return bookRepository.save(book);
    }

    public boolean deleteBook(String isbn){
        bookRepository.deleteById(isbn);
        Optional<Book> book = bookRepository.findById(isbn);
        return book.isEmpty();
    }
}

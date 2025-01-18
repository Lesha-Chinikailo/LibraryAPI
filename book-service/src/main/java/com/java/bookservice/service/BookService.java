package com.java.bookservice.service;

import com.java.bookservice.controller.dto.BookRequestDTO;
import com.java.bookservice.controller.dto.BookResponseDTO;
import com.java.bookservice.exception.BookNotFoundException;
import com.java.bookservice.exception.BookRecordNotDeleteException;
import com.java.bookservice.exception.BookTakenException;
import com.java.bookservice.exception.ServiceUnavailableException;
import com.java.bookservice.mapper.BookMapper;
import com.java.bookservice.models.Book;
import com.java.bookservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final RestTemplate restTemplate;
    private final Properties configProperties;

    @Autowired
    public BookService(BookRepository bookRepository, BookMapper bookMapper, RestTemplate restTemplate,
                       @Qualifier("configProperties") Properties configProperties) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.restTemplate = restTemplate;
        this.configProperties = configProperties;
    }

    public String createBook(BookRequestDTO dto){
        Book book = bookMapper.RequestDTOToBook(dto);

        if(!isServiceAvailable()){
            throw new ServiceUnavailableException("Sorry, library is not available. Try again later");
        }

        Book bookSaved = bookRepository.save(book);

        String url = configProperties.getProperty("url.createBook");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(bookSaved.getISBN(), headers);

        restTemplate.postForEntity(url, requestEntity, String.class);
        return bookSaved.getISBN();
    }

    private boolean isServiceAvailable() {
        String urlHealthCheck = configProperties.getProperty("url.healthcheck");

        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity(urlHealthCheck, Object[].class);
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    private boolean isTakenBook(String isbn) {
        String url = configProperties.getProperty("url.isTakenBook");
        url += isbn;

        ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(url, Boolean.class);
        return Boolean.TRUE.equals(responseEntity.getBody());
    }

    public List<BookResponseDTO> getAllBooks(Long pageNumber, Long pageSize){
        return bookRepository.findAll()
                .stream()
                .skip((pageNumber) * pageSize)
                .limit(pageSize)
                .map(bookMapper::bookToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<BookResponseDTO> getAllFreeBook(Long pageNumber, Long pageSize){
        if(!isServiceAvailable()){
            throw new ServiceUnavailableException("Sorry, library is not available. Try again later");
        }

        String url = configProperties.getProperty("url.getFreeBooks");

        url += "?page=" + pageNumber + "&size=" + pageSize;

        var response = restTemplate.getForEntity(url, String[].class);
        var body = response.getBody();
        List<String> list = body != null
                ? Arrays.stream(body).toList()
                : new ArrayList<>();

        return bookRepository.findAllById(list)
                .stream()
                .map(bookMapper::bookToResponseDTO)
                .toList();
    }

    public Optional<BookResponseDTO> findBookById(String isbn){
        Optional<Book> bookById = bookRepository.findById(isbn);
        return Optional.ofNullable(bookById.map(bookMapper::bookToResponseDTO)
                .orElseThrow(() -> new BookNotFoundException("Unable to find a book with isbn: " + isbn)));
    }

    public BookResponseDTO updateBook(String isbn, BookRequestDTO dto){
        if(findBookById(isbn).isEmpty()){
            throw new BookNotFoundException("Unable to find a book with isbn: " + isbn);
        }

        if(!isServiceAvailable()){
            throw new ServiceUnavailableException("Sorry, library is not available. Try again later");
        }

        if(isTakenBook(isbn)){
            throw new BookTakenException("Sorry, book with isbn: " + isbn + " is taken or not exist");
        }

        Book book = bookRepository.findById(isbn).get();
        book.setTitle(dto.getTitle());
        book.setGenre(dto.getGenre());
        book.setDescription(dto.getDescription());
        book.setAuthor(dto.getAuthor());
        return bookMapper.bookToResponseDTO(bookRepository.save(book));
    }

    public boolean deleteBook(String isbn){
        if(findBookById(isbn).isEmpty()){
            throw new BookNotFoundException("Unable to find a book with isbn: " + isbn);
        }

        if (!isServiceAvailable()) {
            throw new ServiceUnavailableException("Sorry, library is not available. Try again later");
        }

        if (isTakenBook(isbn)) {
            throw new BookTakenException("Sorry, book with isbn: " + isbn + " is taken");
        }

        String urlDelete = configProperties.getProperty("url.deleteBookRecord");
        urlDelete += isbn;

        restTemplate.delete(urlDelete, String.class);

        String urlGet = configProperties.getProperty("url.getBookRecordByISBN");
        urlGet += isbn;
        try{
            var response = restTemplate.getForEntity(urlGet, Object.class);
            if(response.getStatusCode().is2xxSuccessful()){
                throw new BookRecordNotDeleteException("Book record do not delete in the library service");
            }
        }
        catch (HttpClientErrorException e){
            if(e.getStatusCode() != HttpStatus.NOT_FOUND)
                e.printStackTrace();
        }


        bookRepository.deleteById(isbn);
        Optional<Book> book = bookRepository.findById(isbn);
        return book.isEmpty();
    }
}

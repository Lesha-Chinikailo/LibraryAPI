package com.java.bookservice.service;

import com.java.bookservice.client.LibraryServiceClient;
import com.java.bookservice.controller.dto.BookRequestDTO;
import com.java.bookservice.controller.dto.BookResponseDTO;
import com.java.bookservice.exception.BookNotFoundException;
import com.java.bookservice.exception.BookRecordNotDeleteException;
import com.java.bookservice.exception.BookTakenException;
import com.java.bookservice.exception.ServiceUnavailableException;
import com.java.bookservice.mapper.BookMapper;
import com.java.bookservice.models.Book;
import com.java.bookservice.repository.BookRepository;
import com.java.bookservice.controller.dto.BookRecordResponseDTO;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final LibraryServiceClient libraryServiceClient;

    @Autowired
    public BookService(BookRepository bookRepository, BookMapper bookMapper, LibraryServiceClient libraryServiceClient) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.libraryServiceClient = libraryServiceClient;
    }

    public String createBook(BookRequestDTO dto) {
        Book book = bookMapper.RequestDTOToBook(dto);

        if (!isServiceAvailable()) {
            throw new ServiceUnavailableException("Sorry, library is not available. Try again later");
        }

        Book bookSaved = bookRepository.save(book);

        libraryServiceClient.createBookRecord(bookSaved.getISBN());
        return bookSaved.getISBN();
    }

    private boolean isServiceAvailable() {
        ResponseEntity<Object[]> responseEntity = libraryServiceClient.checkHealth(0L, Long.MAX_VALUE);
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    private boolean isTakenBook(String isbn) {

        ResponseEntity<Boolean> responseEntity = libraryServiceClient.isTakenBookById(isbn);
        return Boolean.TRUE.equals(responseEntity.getBody());
    }

    public List<BookResponseDTO> getAllBooks(Long pageNumber, Long pageSize) {
        return bookRepository.findAll()
                .stream()
                .skip((pageNumber) * pageSize)
                .limit(pageSize)
                .map(bookMapper::bookToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<BookResponseDTO> getAllFreeBook(Long pageNumber, Long pageSize) {
        if (!isServiceAvailable()) {
            throw new ServiceUnavailableException("Sorry, library is not available. Try again later");
        }

        ResponseEntity<List<String>> responseEntity = libraryServiceClient.getFreeBookRecordIds(0L, Long.MAX_VALUE);
        List<String> list = responseEntity.getBody();

        return bookRepository.findAllById(list)
                .stream()
                .map(bookMapper::bookToResponseDTO)
                .toList();
    }

    public Optional<BookResponseDTO> findBookById(String isbn) {
        Optional<Book> bookById = bookRepository.findById(isbn);
        return Optional.ofNullable(bookById.map(bookMapper::bookToResponseDTO)
                .orElseThrow(() -> new BookNotFoundException("Unable to find a book with isbn: " + isbn)));
    }

    public BookResponseDTO updateBook(String isbn, BookRequestDTO dto) {
        if (findBookById(isbn).isEmpty()) {
            throw new BookNotFoundException("Unable to find a book with isbn: " + isbn);
        }

        if (!isServiceAvailable()) {
            throw new ServiceUnavailableException("Sorry, library is not available. Try again later");
        }

        if (isTakenBook(isbn)) {
            throw new BookTakenException("Sorry, book with isbn: " + isbn + " is taken or not exist");
        }

        Book book = bookRepository.findById(isbn).get();
        book.setTitle(dto.getTitle());
        book.setGenre(dto.getGenre());
        book.setDescription(dto.getDescription());
        book.setAuthor(dto.getAuthor());
        return bookMapper.bookToResponseDTO(bookRepository.save(book));
    }

    public boolean deleteBook(String isbn) {
        if (findBookById(isbn).isEmpty()) {
            throw new BookNotFoundException("Unable to find a book with isbn: " + isbn);
        }

        if (!isServiceAvailable()) {
            throw new ServiceUnavailableException("Sorry, library is not available. Try again later");
        }

        if (isTakenBook(isbn)) {
            throw new BookTakenException("Sorry, book with isbn: " + isbn + " is taken");
        }

        libraryServiceClient.deleteBookRecord(isbn);

        try {
            ResponseEntity<BookRecordResponseDTO> response = libraryServiceClient.getBookRecordByISBN(isbn);
            if (response.getStatusCode().is2xxSuccessful()) {
                throw new BookRecordNotDeleteException("Book record do not delete in the library service");
            }
        } catch (FeignException e) {
            System.out.println("\n\n\n\n\n\n" + e.getMessage() + "\n\n\n\n\n\n");
        }


        bookRepository.deleteById(isbn);
        Optional<Book> book = bookRepository.findById(isbn);
        return book.isEmpty();
    }
}

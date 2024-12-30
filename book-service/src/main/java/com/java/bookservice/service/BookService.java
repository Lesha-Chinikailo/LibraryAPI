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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor()
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final Validator validator;

    public String createBook(BookRequestDTO dto){
//        Set<ConstraintViolation<BookRequestDTO>> violationResult = validator.validate(dto);
//        if(!violationResult.isEmpty()){
//            throw new ConstraintViolationException(violationResult);
//        }

        Book book = bookMapper.RequestDTOToBook(dto);
        return bookRepository.save(book).getISBN();
    }

    public List<Book> getAllBooks(Long pageNumber, Long pageSize){
        return bookRepository.findAll()
                .stream()
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public List<Book> getAllFreeBook(List<String> isbns, Long pageNumber, Long pageSize){
         return bookRepository.findAllById(isbns)
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

//    public Book findBookByISBN(String ISBN){
//        Book maybeBook = bookRepository.findByISBN(ISBN);
//        if(maybeBook == null){
//            throw new BookNotFoundException("Unable to find book with ISBN: " + ISBN);
//        }
//        return maybeBook;
//    }

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

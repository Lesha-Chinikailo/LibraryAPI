package com.java.bookservice.service;

import com.java.bookservice.controller.dto.BookRequestDTO;
import com.java.bookservice.exception.BookNotFoundException;
import com.java.bookservice.mapper.BookMapper;
import com.java.bookservice.models.Book;
import com.java.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor()
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Long createBook(BookRequestDTO dto){
        Book book = bookMapper.RequestDTOToBook(dto);
        return bookRepository.save(book).getId();
    }

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public Optional<Book> findBookById(Long id){
        Optional<Book> byId = bookRepository.findById(id);
        return Optional.ofNullable(byId
                .orElseThrow(() -> new BookNotFoundException("Unable to find book with id: " + id)));
    }

    public Book findBookByISBN(String ISBN){
        Book maybeBook = bookRepository.findByISBN(ISBN);
        if(maybeBook == null){
            throw new BookNotFoundException("Unable to find book with ISBN: " + ISBN);
        }
        return maybeBook;
    }

    public Book updateBook(Long id, BookRequestDTO dto){
        if(findBookById(id).isEmpty()){
            return null;
        }
        Book book = findBookById(id).get();
        book.setISBN(dto.getISBN());
        book.setTitle(dto.getTitle());
        book.setGenre(dto.getGenre());
        book.setDescription(dto.getDescription());
        book.setAuthor(dto.getAuthor());
        return bookRepository.save(book);
    }

    public boolean deleteBook(Long id){
        bookRepository.deleteById(id);
        Optional<Book> book = bookRepository.findById(id);
        return book.isEmpty();
    }
}

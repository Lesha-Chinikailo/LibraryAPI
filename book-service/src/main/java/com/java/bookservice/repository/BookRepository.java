package com.java.bookservice.repository;

import com.java.bookservice.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, String> {

//    Book findByISBN(String ISBN);
}

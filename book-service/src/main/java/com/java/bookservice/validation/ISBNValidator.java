package com.java.bookservice.validation;

import com.java.bookservice.models.Book;
import com.java.bookservice.repository.BookRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class ISBNValidator implements ConstraintValidator<UniqueISBN, String> {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext constraintValidatorContext) {
        Optional<Book> bookById = bookRepository.findById(isbn);
        return bookById.isPresent();
    }
}

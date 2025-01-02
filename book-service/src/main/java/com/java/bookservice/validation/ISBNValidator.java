package com.java.bookservice.validation;

import com.java.bookservice.models.Book;
import com.java.bookservice.repository.BookRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ISBNValidator implements ConstraintValidator<UniqueISBN, String> {

    private BookRepository bookRepository;

    @Autowired
    public ISBNValidator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public ISBNValidator() {
        this.bookRepository = null;
    }

    @Override
    public void initialize(UniqueISBN constraintAnnotation) {
        if(bookRepository == null) {
            this.bookRepository = (BookRepository) ContextProvider.getBean(BookRepository.class);
        }
    }

    private boolean isDigit(String string) {
        return string.matches("[0-9]+");
    }

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext constraintValidatorContext) {
        if(isbn == null || isbn.isEmpty()){
            return false;
        }
//        if(isDigit(isbn)){
//            return false;
//        }
        Optional<Book> bookById = bookRepository.findById(isbn);
        System.out.println("\n\n\n\n" + bookById + "\n\n\n\n");
        return bookById.isEmpty();
    }
}

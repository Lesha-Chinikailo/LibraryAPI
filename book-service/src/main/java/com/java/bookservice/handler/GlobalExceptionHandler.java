package com.java.bookservice.handler;

import com.java.bookservice.exception.BookNotFoundException;
import com.java.bookservice.exception.BookRecordNotDeleteException;
import com.java.bookservice.exception.BookTakenException;
import com.java.bookservice.exception.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({BookNotFoundException.class})
    public ResponseEntity<Object> handleBookNotFoundException(BookNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler({BookTakenException.class})
    public ResponseEntity<Object> handleBookTakenException(BookTakenException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler({ServiceUnavailableException.class})
    public ResponseEntity<Object> handleServiceUnavailableException(ServiceUnavailableException exception) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(exception.getMessage());
    }

    @ExceptionHandler({BookRecordNotDeleteException.class})
    public ResponseEntity<Object> handleBookRecordNotDeleteException(BookRecordNotDeleteException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
}

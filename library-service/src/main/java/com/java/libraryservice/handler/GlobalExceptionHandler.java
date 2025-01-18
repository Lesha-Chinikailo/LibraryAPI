package com.java.libraryservice.handler;

import com.java.libraryservice.exception.BookRecordNotFoundException;
import com.java.libraryservice.exception.BookReturnedException;
import com.java.libraryservice.exception.BookTakenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({BookRecordNotFoundException.class})
    public ResponseEntity<Object> handleBookNotFoundException(BookRecordNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler({BookTakenException.class})
    public ResponseEntity<Object> handleBookTakenException(BookTakenException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    @ExceptionHandler({BookReturnedException.class})
    public ResponseEntity<Object> handleBookReturnedException(BookReturnedException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
}

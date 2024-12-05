package com.java.libraryservice.exception;

public class BookRecordNotFoundException extends RuntimeException {
    public BookRecordNotFoundException(String message) {
        super(message);
    }
}

package com.java.libraryservice.exception;

public class BookReturnedException extends RuntimeException {
    public BookReturnedException(String message) {
        super(message);
    }
}

package com.java.libraryservice.exception;

public class BookTakenException extends RuntimeException {
    public BookTakenException(String message) {
        super(message);
    }
}

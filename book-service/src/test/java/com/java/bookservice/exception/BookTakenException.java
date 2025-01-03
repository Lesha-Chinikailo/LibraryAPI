package com.java.bookservice.exception;

public class BookTakenException extends RuntimeException {
    public BookTakenException(String message) {
        super(message);
    }
}

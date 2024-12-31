package com.java.bookservice.exception;

public class BookRecordNotDeleteException extends RuntimeException {
    public BookRecordNotDeleteException(String message) {
        super(message);
    }
}

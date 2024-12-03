package com.java.bookservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookRequestDTO {

    private String ISBN;

    private String title;

    private String genre;

    private String description;

    private String author;
}

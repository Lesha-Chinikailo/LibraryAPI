package com.java.bookservice.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookRequestDTO {

    @JsonProperty("isbn")
    private String ISBN;

    private String title;

    private String genre;

    private String description;

    private String author;
}

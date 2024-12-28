package com.java.bookservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDTO {

    private String ISBN;

    private String title;

    private String genre;

    private String description;

    private String author;
}

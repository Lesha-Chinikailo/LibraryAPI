package com.java.bookservice.controller.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponseDTO {

    private String ISBN;

    private String title;

    private String genre;

    private String description;

    private String author;
}

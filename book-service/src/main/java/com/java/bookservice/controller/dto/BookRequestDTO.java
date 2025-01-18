package com.java.bookservice.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.bookservice.validation.UniqueISBN;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BookRequestDTO {

    @JsonProperty("isbn")
    @NotNull
    @Size(max = 13, min = 9)
    @UniqueISBN
    private String ISBN;

    @NotNull
    @Size(max = 50)
    private String title;

    @NotNull
    @Size(max = 30)
    private String genre;

    @Size(max = 254)
    private String description;

    @NotNull
    @Size(max = 100)
    private String author;
}

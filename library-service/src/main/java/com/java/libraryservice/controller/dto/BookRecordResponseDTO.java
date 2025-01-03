package com.java.libraryservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRecordResponseDTO {

    private String ISBN;

    private LocalDateTime dateTimeTakeOfBook;

    private LocalDateTime dateTimeReturnOfBook;
}

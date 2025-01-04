package com.java.libraryservice.controller.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRecordResponseDTO {

    private String ISBN;

    private LocalDateTime dateTimeTakeOfBook;

    private LocalDateTime dateTimeReturnOfBook;
}

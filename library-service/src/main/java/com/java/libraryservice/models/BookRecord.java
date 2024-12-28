package com.java.libraryservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "book_record")
public class BookRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "book_id")
    @NotNull
    private Long bookId;

    @Column(name = "date_time_take_of_book")
    private LocalDateTime dateTimeTakeOfBook;

    @Column(name = "date_time_return_of_book")
    private LocalDateTime dateTimeReturnOfBook;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = id.hashCode();
        result = prime * result + bookId.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        BookRecord bookRecord = (BookRecord) obj;
        if(!bookRecord.getId().equals(this.id)) {
            return false;
        }

        return bookRecord.getBookId().equals(this.getBookId());
    }
}

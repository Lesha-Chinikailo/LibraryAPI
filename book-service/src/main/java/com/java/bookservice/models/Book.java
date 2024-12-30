package com.java.bookservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "book")
public class Book {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;

    @Id
    @NotNull
    @Column(name = "isbn")
    @Size(max = 13, min = 9)
    private String ISBN;

    @Column(name = "title")
    @NotNull
    @Size(max = 50)
    private String title;

    @Column(name = "genre")
    @NotNull
    @Size(max = 30)
    private String genre;

    @Column(name = "description")
    @Size(max = 254)
    private String description;

    @Column(name = "author")
    @NotNull
    @Size(max = 100)
    private String author;

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime + ISBN.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        Book book = (Book) obj;

        return book.getISBN().equals(this.getISBN());
    }
}

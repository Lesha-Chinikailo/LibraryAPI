package com.java.libraryservice.repository;

import com.java.libraryservice.models.BookRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRecordRepository extends JpaRepository<BookRecord, Long> {

    Optional<BookRecord> findByISBN(String ISBN);

}

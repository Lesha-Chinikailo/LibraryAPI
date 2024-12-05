package com.java.libraryservice.repository;

import com.java.libraryservice.models.BookRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRecordRepository extends JpaRepository<BookRecord, Long> {

}

package com.java.libraryservice.service;

import com.java.libraryservice.exception.BookRecordNotFoundException;
import com.java.libraryservice.models.BookRecord;
import com.java.libraryservice.repository.BookRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookRecordService {

    private final BookRecordRepository bookRecordRepository;

    public List<BookRecord> findAll() {
        return bookRecordRepository.findAll();
    }

    public List<BookRecord> findAllFreeBook() {
        return bookRecordRepository.findAll()
                .stream()
                .filter(b -> b.getDateTimeTakeOfBook() == null)
                .toList();
    }
    public List<Long> findAllFreeBookIds() {
        return bookRecordRepository.findAll()
                .stream()
                .filter(b -> b.getDateTimeTakeOfBook() == null)
                .map(BookRecord::getBookId)
                .toList();
    }


    public Long addBookRecord(Long bookId) {
        BookRecord record = BookRecord.builder()
                .bookId(bookId)
                .build();
        return bookRecordRepository.save(record).getId();
    }

    public Long TakeBook(Long bookId) {
        Optional<BookRecord> maybeBookRecord = bookRecordRepository.findById(bookId);
        if(maybeBookRecord.isPresent()) {
            BookRecord bookRecord = maybeBookRecord.get();
            LocalDateTime dateTimeNow = LocalDateTime.now();
            bookRecord.setDateTimeTakeOfBook(dateTimeNow);
            bookRecord.setDateTimeReturnOfBook(dateTimeNow.plusDays(30));
            return bookRecordRepository.save(bookRecord).getId();
        }
        throw new BookRecordNotFoundException("Unable to find book with book id:" + bookId);
    }

    public Long ReturnBook(Long bookId) {
        Optional<BookRecord> maybeBookRecord = bookRecordRepository.findById(bookId);
        if(maybeBookRecord.isPresent()) {
            BookRecord bookRecord = maybeBookRecord.get();
            bookRecord.setDateTimeReturnOfBook(null);
            bookRecord.setDateTimeTakeOfBook(null);
            return bookRecordRepository.save(bookRecord).getId();
        }
        throw new BookRecordNotFoundException("Unable to find book with book id:" + bookId);
    }
}

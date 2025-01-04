package com.java.libraryservice.service;

import com.java.libraryservice.controller.dto.BookRecordResponseDTO;
import com.java.libraryservice.exception.BookRecordNotFoundException;
import com.java.libraryservice.mapper.BookRecordMapper;
import com.java.libraryservice.models.BookRecord;
import com.java.libraryservice.repository.BookRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookRecordService {

    private final BookRecordRepository bookRecordRepository;
    private final BookRecordMapper bookRecordMapper;

    public List<BookRecord> findAll(Long pageNumber, Long pageSize) {
        return bookRecordRepository.findAll()
                .stream()
                .skip((pageNumber) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public List<BookRecord> findAllFreeBook(Long pageNumber, Long pageSize) {
        return bookRecordRepository.findAll()
                .stream()
                .filter(b -> b.getDateTimeTakeOfBook() == null)
                .skip((pageNumber) * pageSize)
                .limit(pageSize)
                .toList();
    }
    public List<String> findAllFreeBookIds(Long pageNumber, Long pageSize) {
        return bookRecordRepository.findAll()
                .stream()
                .filter(b -> b.getDateTimeTakeOfBook() == null)
                .skip((pageNumber) * pageSize)
                .limit(pageSize)
                .map(BookRecord::getISBN)
                .toList();
    }


    public Long addBookRecord(String isbn) {
        BookRecord record = BookRecord.builder()
                .ISBN(isbn)
                .build();
        return bookRecordRepository.save(record).getId();
    }

    public Optional<BookRecord> findBookRecordById(Long id) {
        Optional<BookRecord> bookRecordById = bookRecordRepository.findById(id);
        return Optional.of(bookRecordById)
                .orElseThrow(() -> new BookRecordNotFoundException("Unable to find book record with id: " + id));
    }

    public Optional<BookRecord> findBookRecordByISBN(String isbn) {
        return bookRecordRepository.findByISBN(isbn);
    }

    public boolean isTakenBook(String isbn) {
        Optional<BookRecord> byISBN = bookRecordRepository.findByISBN(isbn);
        if(byISBN.isPresent()) {
            BookRecord bookRecord = byISBN.get();
            return bookRecord.getDateTimeReturnOfBook() != null;
        }
        return true;
    }

    public BookRecordResponseDTO takeBook(String isbn) {
        if(isTakenBook(isbn)) {
            return null;
        }
        Optional<BookRecord> maybeBookRecord = bookRecordRepository.findByISBN(isbn);
        if(maybeBookRecord.isPresent()) {
            BookRecord bookRecord = maybeBookRecord.get();
            LocalDateTime dateTimeNow = LocalDateTime.now();
            bookRecord.setDateTimeTakeOfBook(dateTimeNow);
            bookRecord.setDateTimeReturnOfBook(dateTimeNow.plusDays(30));
            return bookRecordMapper.bookRecordToResponseDTO(bookRecordRepository.save(bookRecord));
        }
        return null;
    }

    public BookRecordResponseDTO returnBook(String isbn) {
        Optional<BookRecord> maybeBookRecord = bookRecordRepository.findByISBN(isbn);
        if(maybeBookRecord.isPresent()) {
            BookRecord bookRecord = maybeBookRecord.get();
            if(bookRecord.getDateTimeTakeOfBook() == null) {
                return null;
            }
            bookRecord.setDateTimeReturnOfBook(null);
            bookRecord.setDateTimeTakeOfBook(null);
            return bookRecordMapper.bookRecordToResponseDTO(bookRecordRepository.save(bookRecord));
        }
        throw new BookRecordNotFoundException("Unable to find book with isbn:" + isbn);
    }

    @Transactional
    public boolean deleteBookRecord(String isbn) {
        bookRecordRepository.deleteByISBN(isbn);
        Optional<BookRecord> byISBN = bookRecordRepository.findByISBN(isbn);
        return byISBN.isEmpty();
    }
}

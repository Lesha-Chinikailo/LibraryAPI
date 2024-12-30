package com.java.libraryservice.service;

import com.java.libraryservice.controller.dto.BookRecordResponseDTO;
import com.java.libraryservice.exception.BookRecordNotFoundException;
import com.java.libraryservice.mapper.BookRecordMapper;
import com.java.libraryservice.models.BookRecord;
import com.java.libraryservice.repository.BookRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public List<BookRecord> findAllFreeBook(Long pageNumber, Long pageSize) {
        return bookRecordRepository.findAll()
                .stream()
                .filter(b -> b.getDateTimeTakeOfBook() == null)
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .toList();
    }
    public List<String> findAllFreeBookIds(Long pageNumber, Long pageSize) {
        return bookRecordRepository.findAll()
                .stream()
                .filter(b -> b.getDateTimeTakeOfBook() == null)
                .skip((pageNumber - 1) * pageSize)
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

    public BookRecordResponseDTO takeBook(String isbn) {
        Optional<BookRecord> maybeBookRecord = bookRecordRepository.findByISBN(isbn);
        if(maybeBookRecord.isPresent()) {
            BookRecord bookRecord = maybeBookRecord.get();
            if(bookRecord.getDateTimeTakeOfBook() != null) {
                return null;
            }
            LocalDateTime dateTimeNow = LocalDateTime.now();
            bookRecord.setDateTimeTakeOfBook(dateTimeNow);
            bookRecord.setDateTimeReturnOfBook(dateTimeNow.plusDays(30));
            return bookRecordMapper.bookRecordToResponseDTO(bookRecordRepository.save(bookRecord));
        }
        throw new BookRecordNotFoundException("Unable to find book with isbn:" + isbn);
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
}

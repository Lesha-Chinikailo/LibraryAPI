package com.java.libraryservice.service;

import com.java.libraryservice.controller.dto.BookRecordResponseDTO;
import com.java.libraryservice.exception.BookRecordNotFoundException;
import com.java.libraryservice.exception.BookReturnedException;
import com.java.libraryservice.exception.BookTakenException;
import com.java.libraryservice.mapper.BookRecordMapper;
import com.java.libraryservice.models.BookRecord;
import com.java.libraryservice.repository.BookRecordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookRecordService {

    private final BookRecordRepository bookRecordRepository;
    private final BookRecordMapper bookRecordMapper;
    private final JdbcTemplate jdbcTemplate;
    private String resetSequenceId = "ALTER SEQUENCE book_record_id_seq RESTART WITH %d";

    @PostConstruct
    public void init() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("classpath:/db/data/book-record-insert-data.csv").toURI()));
            jdbcTemplate.execute(resetSequenceId.formatted(lines.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<BookRecordResponseDTO> findAll(Long pageNumber, Long pageSize) {
        return bookRecordRepository.findAll()
                .stream()
                .skip((pageNumber) * pageSize)
                .limit(pageSize)
                .map(bookRecordMapper::bookRecordToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<BookRecordResponseDTO> findAllFreeBook(Long pageNumber, Long pageSize) {
        return bookRecordRepository.findAll()
                .stream()
                .filter(b -> b.getDateTimeTakeOfBook() == null)
                .skip((pageNumber) * pageSize)
                .limit(pageSize)
                .map(bookRecordMapper::bookRecordToResponseDTO)
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

    public BookRecordResponseDTO findBookRecordById(Long id) {
        Optional<BookRecord> bookRecordById = bookRecordRepository.findById(id);
        return bookRecordById.map(bookRecordMapper::bookRecordToResponseDTO)
                .orElseThrow(() -> new BookRecordNotFoundException("Unable to find a book record with id:" + id));
    }

    public BookRecordResponseDTO findBookRecordByISBN(String isbn) {
        return bookRecordRepository.findByISBN(isbn)
                .map(bookRecordMapper::bookRecordToResponseDTO)
                .orElseThrow(() -> new BookRecordNotFoundException("Unable to find a book record with id:" + isbn));
    }

    public boolean isTakenBook(String isbn) {
        Optional<BookRecord> byISBN = bookRecordRepository.findByISBN(isbn);
        if(byISBN.isPresent()) {
            BookRecord bookRecord = byISBN.get();
            return bookRecord.getDateTimeReturnOfBook() != null;
        }
        throw new BookRecordNotFoundException("Unable to find a book record with isbn:" + isbn);
    }

    public BookRecordResponseDTO takeBook(String isbn) {
        if(isTakenBook(isbn)) {
            throw new BookTakenException("Book with isbn" + isbn + " already taken");
        }
        Optional<BookRecord> maybeBookRecord = bookRecordRepository.findByISBN(isbn);
        if(maybeBookRecord.isPresent()) {
            BookRecord bookRecord = maybeBookRecord.get();
            LocalDateTime dateTimeNow = LocalDateTime.now();
            bookRecord.setDateTimeTakeOfBook(dateTimeNow);
            bookRecord.setDateTimeReturnOfBook(dateTimeNow.plusDays(30));
            return bookRecordMapper.bookRecordToResponseDTO(bookRecordRepository.save(bookRecord));
        }
        throw new BookRecordNotFoundException("Unable to find book with isbn:" + isbn);
    }

    public BookRecordResponseDTO returnBook(String isbn) {
        if(!isTakenBook(isbn)) {
            throw new BookReturnedException("Book with isbn" + isbn + "already returned");
        }
        Optional<BookRecord> maybeBookRecord = bookRecordRepository.findByISBN(isbn);
        if(maybeBookRecord.isPresent()) {
            BookRecord bookRecord = maybeBookRecord.get();
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

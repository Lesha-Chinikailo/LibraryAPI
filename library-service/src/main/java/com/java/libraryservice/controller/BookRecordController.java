package com.java.libraryservice.controller;

import com.java.libraryservice.controller.dto.BookRecordResponseDTO;
import com.java.libraryservice.mapper.BookRecordMapper;
import com.java.libraryservice.models.BookRecord;
import com.java.libraryservice.service.BookRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BookRecordController {

    private final BookRecordService bookRecordService;
    private final BookRecordMapper bookRecordMapper;

    @GetMapping("/free")
    public ResponseEntity<List<BookRecordResponseDTO>> getFreeBookRecords(@RequestParam(defaultValue = "0") Long page,
                                                                          @RequestParam(defaultValue = "10") Long size) {
        return new ResponseEntity<>(
                bookRecordService.findAllFreeBook(page, size),
                HttpStatus.OK);
    }

    @GetMapping("/isTaken/{isbn}")
    public ResponseEntity<Boolean> isTakenBookById(@PathVariable String isbn) {
        return new ResponseEntity<>(bookRecordService.isTakenBook(isbn), HttpStatus.OK);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookRecordResponseDTO> getBookRecordByISBN(@PathVariable String isbn) {
        Optional<BookRecordResponseDTO> bookRecordByISBN = bookRecordService.findBookRecordByISBN(isbn);
        return bookRecordByISBN
                .map(bookRecord -> new ResponseEntity<>(
                        bookRecord,
                        HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(
                        new BookRecordResponseDTO(),
                        HttpStatus.NOT_FOUND));

    }

    @GetMapping("/")
    public ResponseEntity<List<BookRecordResponseDTO>> getBookRecords(@RequestParam(defaultValue = "0") Long page,
                                                                      @RequestParam(defaultValue = "10") Long size) {
        if (page < 0 || size < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(
                bookRecordService.findAll(page, size),
                HttpStatus.OK);
    }

    @GetMapping("/free/ids")
    public ResponseEntity<List<String>> getFreeBookRecordIds(@RequestParam(defaultValue = "0") Long page, @RequestParam(defaultValue = "10") Long size) {

        if (page < 0 || size < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(bookRecordService.findAllFreeBookIds(page, size), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<BookRecordResponseDTO> createBookRecord(@RequestBody String isbn) {
        Long bookRecordId = bookRecordService.addBookRecord(isbn);
        Optional<BookRecordResponseDTO> bookRecordResponseById = bookRecordService.findBookRecordById(bookRecordId);
        return bookRecordResponseById
                .map(bookRecord -> new ResponseEntity<>(bookRecord, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @PutMapping("take/{isbn}")
    public ResponseEntity<BookRecordResponseDTO> takeBookRecord(@PathVariable String isbn) {
        BookRecordResponseDTO bookRecordResponseDTO = bookRecordService.takeBook(isbn);
        if (bookRecordResponseDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bookRecordResponseDTO, HttpStatus.OK);
    }

    @PutMapping("return/{isbn}")
    public ResponseEntity<BookRecordResponseDTO> returnBookRecord(@PathVariable String isbn) {
        BookRecordResponseDTO bookRecordResponseDTO = bookRecordService.returnBook(isbn);
        if (bookRecordResponseDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bookRecordResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<String> deleteBookRecord(@PathVariable String isbn) {
        boolean isDeleted = bookRecordService.deleteBookRecord(isbn);
        if (isDeleted) {
            return ResponseEntity.ok("Book with isbn: " + isbn + " has been deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to delete book with isbn: " + isbn);
        }
    }
}

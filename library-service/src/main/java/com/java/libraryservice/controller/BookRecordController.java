package com.java.libraryservice.controller;

import com.java.libraryservice.controller.dto.BookRecordResponseDTO;
import com.java.libraryservice.exception.BookRecordNotFoundException;
import com.java.libraryservice.mapper.BookRecordMapper;
import com.java.libraryservice.models.BookRecord;
import com.java.libraryservice.service.BookRecordService;
import jakarta.servlet.http.HttpServletResponse;
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
    public List<BookRecordResponseDTO> getFreeBookRecords(@RequestParam(defaultValue = "0") Long page,
                                                          @RequestParam(defaultValue = "10") Long size) {
        return bookRecordService.findAllFreeBook(page, size)
                .stream()
                .map(bookRecordMapper::bookRecordToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/isTaken/{isbn}")
    public ResponseEntity<Boolean> getFreeBookRecord(@PathVariable String isbn) {
        return new ResponseEntity<>(bookRecordService.isTakenBook(isbn), HttpStatus.OK);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookRecordResponseDTO> getBookRecordByISBN(@PathVariable String isbn) {
        Optional<BookRecord> bookRecordByISBN = bookRecordService.findBookRecordByISBN(isbn);
        return bookRecordByISBN
                .map(bookRecord -> new ResponseEntity<>(
                bookRecordMapper.bookRecordToResponseDTO(bookRecord),
                HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(
                new BookRecordResponseDTO(),
                HttpStatus.NOT_FOUND));

    }

    @GetMapping("/")
    public List<BookRecordResponseDTO> getBookRecords(@RequestParam(defaultValue = "0") Long page,
                                                      @RequestParam(defaultValue = "10") Long size) {
        if (page < 0 || size < 0)
        {
            throw new IllegalArgumentException("Page and size parameters must be non-negative.");
        }
        return bookRecordService.findAll(page, size)
                .stream()
                .map(bookRecordMapper::bookRecordToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/free/ids")
    public List<String> getFreeBookRecordIds(@RequestParam(defaultValue = "0") Long page,
                                           @RequestParam(defaultValue = "10") Long size) {
        return bookRecordService.findAllFreeBookIds(page, size);
    }

    @PostMapping("/")
    public BookRecordResponseDTO createBookRecord(@RequestBody String isbn, HttpServletResponse response) {
        Long bookRecordId = bookRecordService.addBookRecord(isbn);
        Optional<BookRecord> bookRecordById = bookRecordService.findBookRecordById(bookRecordId);
        if(bookRecordById.isPresent()) {
            return bookRecordMapper.bookRecordToResponseDTO(bookRecordById.get());
        }
        else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new BookRecordResponseDTO();
        }
    }

    @PutMapping("take/{isbn}")
    public BookRecordResponseDTO takeBookRecord(@PathVariable String isbn, HttpServletResponse response) {
        BookRecordResponseDTO bookRecordResponseDTO = bookRecordService.takeBook(isbn);
        if(bookRecordResponseDTO == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return bookRecordResponseDTO;
    }

    @PutMapping("return/{isbn}")
    public BookRecordResponseDTO returnBookRecord(@PathVariable String isbn, HttpServletResponse response) {
        BookRecordResponseDTO bookRecordResponseDTO = bookRecordService.returnBook(isbn);
        if(bookRecordResponseDTO == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return bookRecordResponseDTO;
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<String> deleteBookRecord(@PathVariable String isbn) {
        boolean isDeleted = bookRecordService.deleteBookRecord(isbn);
        if(isDeleted){
            return ResponseEntity.ok("Book with isbn: " + isbn + " has been deleted successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete book with isbn: " + isbn);
        }
    }
}

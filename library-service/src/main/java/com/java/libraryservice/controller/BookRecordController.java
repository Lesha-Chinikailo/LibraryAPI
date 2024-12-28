package com.java.libraryservice.controller;

import com.java.libraryservice.controller.dto.BookRecordResponseDTO;
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
    public List<BookRecordResponseDTO> getFreeBookRecords() {
        return bookRecordService.findAllFreeBook()
                .stream()
                .map(bookRecordMapper::bookRecordToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/")
    public List<BookRecordResponseDTO> getBookRecords() {
        return bookRecordService.findAll()
                .stream()
                .map(bookRecordMapper::bookRecordToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/free/ids")
    public List<Long> getFreeBookRecordIds() {
        return bookRecordService.findAllFreeBookIds();
    }

    @PostMapping("/")
    public BookRecordResponseDTO createBookRecord(@RequestBody Long bookId, HttpServletResponse response) {
        Long bookRecordId = bookRecordService.addBookRecord(bookId);
        Optional<BookRecord> bookRecordById = bookRecordService.findBookRecordById(bookRecordId);
        if(bookRecordById.isPresent()) {
            return bookRecordMapper.bookRecordToResponseDTO(bookRecordById.get());
        }
        else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new BookRecordResponseDTO();
        }
    }

    @PutMapping("take/{id}")
    public BookRecordResponseDTO takeBookRecord(@PathVariable Long id, HttpServletResponse response) {
        BookRecordResponseDTO bookRecordResponseDTO = bookRecordService.takeBook(id);
        if(bookRecordResponseDTO == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return bookRecordResponseDTO;

    }

    @PutMapping("return/{id}")
    public BookRecordResponseDTO returnBookRecord(@PathVariable Long id, HttpServletResponse response) {
        BookRecordResponseDTO bookRecordResponseDTO = bookRecordService.returnBook(id);
        if(bookRecordResponseDTO == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return bookRecordResponseDTO;
    }
}

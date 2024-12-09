package com.java.libraryservice.controller;

import com.java.libraryservice.controller.dto.BookRecordResponseDTO;
import com.java.libraryservice.mapper.BookRecordMapper;
import com.java.libraryservice.models.BookRecord;
import com.java.libraryservice.service.BookRecordService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookRecordController {

    private final BookRecordService bookRecordService;
    private final BookRecordMapper bookRecordMapper;

    @GetMapping("/free")
    public List<BookRecord> getFreeBookRecords() {
        return bookRecordService.findAllFreeBook();
    }

    @GetMapping("/")
    public List<BookRecord> getBookRecords() {
        return bookRecordService.findAll();
    }

    @GetMapping("/free/ids")
    public List<Long> getFreeBookRecordIds() {
        return bookRecordService.findAllFreeBookIds();
    }

    @PostMapping("/")
    public Long createBookRecord(@RequestBody Long bookId) {
        return bookRecordService.addBookRecord(bookId);
    }

    @PutMapping("take/{id}")
    public BookRecordResponseDTO takeBookRecord(@PathVariable Long id, HttpServletResponse response) {
        BookRecordResponseDTO bookRecordResponseDTO = bookRecordService.TakeBook(id);
        if(bookRecordResponseDTO == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return bookRecordResponseDTO;

    }

    @PutMapping("return/{id}")
    public BookRecordResponseDTO returnBookRecord(@PathVariable Long id, HttpServletResponse response) {
        BookRecordResponseDTO bookRecordResponseDTO = bookRecordService.ReturnBook(id);
        if(bookRecordResponseDTO == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return bookRecordResponseDTO;
    }
}

package com.java.libraryservice.controller;

import com.java.libraryservice.models.BookRecord;
import com.java.libraryservice.service.BookRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookRecordController {

    private final BookRecordService bookRecordService;

    @GetMapping("/free")
    public List<BookRecord> getFreeBookRecords() {
        return bookRecordService.findAllFreeBook();
    }

    @GetMapping("/free/id")
    public List<Long> getFreeBookRecordIds() {
        return bookRecordService.findAllFreeBookIds();
    }
}

package com.java.libraryservice.controller;

import com.java.libraryservice.controller.dto.BookRecordResponseDTO;
import com.java.libraryservice.mapper.BookRecordMapper;
import com.java.libraryservice.models.BookRecord;
import com.java.libraryservice.service.BookRecordService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/")
    public List<BookRecordResponseDTO> getBookRecords(@RequestParam(defaultValue = "0") Long page,
                                                      @RequestParam(defaultValue = "10") Long size) {
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
}

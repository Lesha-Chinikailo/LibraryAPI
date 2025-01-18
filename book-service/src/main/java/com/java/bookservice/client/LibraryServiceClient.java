package com.java.bookservice.client;

import com.java.bookservice.controller.dto.BookRecordResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "library-service", url = "${service.url}")
public interface LibraryServiceClient {

    @GetMapping("/free")
    ResponseEntity<List<BookRecordResponseDTO>> getFreeBookRecords(@RequestParam(defaultValue = "0") Long page,
                                                                   @RequestParam(defaultValue = "10") Long size);

    @GetMapping("/isTaken/{isbn}")
    ResponseEntity<Boolean> isTakenBookById(@PathVariable String isbn);

    @GetMapping("/isbn/{isbn}")
    ResponseEntity<BookRecordResponseDTO> getBookRecordByISBN(@PathVariable String isbn);

    @GetMapping("/")
    ResponseEntity<List<BookRecordResponseDTO>> getBookRecords(@RequestParam(defaultValue = "0") Long page,
                                                               @RequestParam(defaultValue = "10") Long size);

    @GetMapping("/")
    ResponseEntity<Object[]> checkHealth(@RequestParam(defaultValue = "0") Long page,
                                         @RequestParam(defaultValue = "10") Long size);

    @GetMapping("/free/ids")
    ResponseEntity<List<String>> getFreeBookRecordIds(@RequestParam(defaultValue = "0") Long page, @RequestParam(defaultValue = "10") Long size);

    @PostMapping("/")
    ResponseEntity<BookRecordResponseDTO> createBookRecord(@RequestBody String isbn);

    @PutMapping("take/{isbn}")
    ResponseEntity<BookRecordResponseDTO> takeBookRecord(@PathVariable String isbn);

    @PutMapping("return/{isbn}")
    ResponseEntity<BookRecordResponseDTO> returnBookRecord(@PathVariable String isbn);

    @DeleteMapping("/{isbn}")
    ResponseEntity<String> deleteBookRecord(@PathVariable String isbn);
}

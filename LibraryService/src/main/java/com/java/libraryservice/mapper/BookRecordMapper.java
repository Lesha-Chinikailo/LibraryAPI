package com.java.libraryservice.mapper;

import com.java.libraryservice.controller.dto.BookRecordResponseDTO;
import com.java.libraryservice.models.BookRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookRecordMapper {

    BookRecordMapper INSTANCE = Mappers.getMapper(BookRecordMapper.class);

    //Book RequestDTOToBook(BookRequestDTO dto);

    BookRecordResponseDTO bookToResponseDTO(BookRecord bookRecord);
}

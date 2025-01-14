package com.java.authservice.mapper;

import com.java.authservice.controller.dto.UserRequestDTO;
import com.java.authservice.controller.dto.UserResponseDTO;
import com.java.authservice.entity.UserCredential;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserCredential requestDTOToUser(UserRequestDTO dto);

    UserResponseDTO userToResponseDTO(UserCredential userCredential);
}

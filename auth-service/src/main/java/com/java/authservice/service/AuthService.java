package com.java.authservice.service;

import com.java.authservice.controller.dto.UserRequestDTO;
import com.java.authservice.controller.dto.UserResponseDTO;
import com.java.authservice.entity.UserCredential;
import com.java.authservice.mapper.UserMapper;
import com.java.authservice.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserCredentialRepository userCredentialRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponseDTO saveUser(UserRequestDTO dto) {
        UserCredential userCredential = userMapper.requestDTOToUser(dto);
        userCredential.setPassword(passwordEncoder.encode(userCredential.getPassword()));
        UserCredential savedUserCredential = userCredentialRepository.save(userCredential);
        return userMapper.userToResponseDTO(savedUserCredential);
    }

    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
}

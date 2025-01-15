package com.java.authservice.service;

import com.java.authservice.controller.dto.UserRequestDTO;
import com.java.authservice.controller.dto.UserResponseDTO;
import com.java.authservice.entity.User;
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
    private final String BEARER_TOKEN_PREFIX = "Bearer ";

    public UserResponseDTO saveUser(UserRequestDTO dto) {
        User user = userMapper.requestDTOToUser(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userCredentialRepository.save(user);
        return userMapper.userToResponseDTO(savedUser);
    }

    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    public void validateToken(String token) {
        if(token.startsWith(BEARER_TOKEN_PREFIX)){
            token = token.substring(BEARER_TOKEN_PREFIX.length());
        }
        jwtService.validateToken(token);
    }
}

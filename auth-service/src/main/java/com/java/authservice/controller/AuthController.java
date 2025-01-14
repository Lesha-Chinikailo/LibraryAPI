package com.java.authservice.controller;

import com.java.authservice.controller.dto.UserRequestDTO;
import com.java.authservice.controller.dto.UserResponseDTO;
import com.java.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> addNewUser(@RequestBody UserRequestDTO userRequestDTO) {
        return new ResponseEntity<>(
                authService.saveUser(userRequestDTO),
                HttpStatus.OK);
    }

    @GetMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody UserRequestDTO userRequestDTO){
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userRequestDTO.getUsername(),
                userRequestDTO.getPassword()));
        if(authenticate.isAuthenticated()){
            return new ResponseEntity<>(
                    authService.generateToken(userRequestDTO.getUsername()),
                    HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("invalid access", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        authService.validateToken(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

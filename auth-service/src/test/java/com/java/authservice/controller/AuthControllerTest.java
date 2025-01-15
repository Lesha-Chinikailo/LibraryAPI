package com.java.authservice.controller;

import com.java.authservice.controller.dto.UserRequestDTO;
import com.java.authservice.controller.dto.UserResponseDTO;
import com.java.authservice.entity.User;
import com.java.authservice.mapper.UserMapper;
import com.java.authservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    @Mock
    private Authentication authentication;

    private String token = "123456789";
    private UserResponseDTO fullFieldUserResponseDTO;
    private UserRequestDTO fullFieldUserRequestDTO;
    private User fullFieldUser;

    @BeforeEach
    public void prepare() {
//        MockitoAnnotations.openMocks(this);
        fullFieldUserResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .username("username 1")
                .password("password 1")
                .build();
        fullFieldUserRequestDTO = UserRequestDTO.builder()
                .username("username 1")
                .password("password 1")
                .build();
        fullFieldUser = User.builder()
                .id(1L)
                .username("username 1")
                .password("password 1")
                .build();
    }

    @Test
    public void addNewUser_success(){
        doReturn(fullFieldUserResponseDTO).when(authService).saveUser(fullFieldUserRequestDTO);

        ResponseEntity<UserResponseDTO> response = authController.addNewUser(fullFieldUserRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getToken_success(){
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated())
                .thenReturn(true);
        doReturn(token).when(authService).generateToken(fullFieldUserRequestDTO.getUsername());

        ResponseEntity<String> tokenResponse = authController.getToken(fullFieldUserRequestDTO);
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tokenResponse.getBody()).isEqualTo(token);
    }

    @Test
    public void getToken_fail(){
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated())
                .thenReturn(false);

        ResponseEntity<String> tokenResponse = authController.getToken(fullFieldUserRequestDTO);
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.getStatusCode()).isNotEqualTo(HttpStatus.OK);
    }

    @Test
    public void validateToken_success(){
//        doThrow(Exception.class).when(authService).validateToken(token);
        doNothing().when(authService).validateToken(token);

        ResponseEntity<?> responseEntity = authController.validateToken(token);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void validateToken_fail(){
        doThrow(RuntimeException.class).when(authService).validateToken(token);

        ResponseEntity<?> responseEntity = authController.validateToken(token);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
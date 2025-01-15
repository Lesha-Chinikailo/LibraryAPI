package com.java.authservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    private String username;
    private String password;
}

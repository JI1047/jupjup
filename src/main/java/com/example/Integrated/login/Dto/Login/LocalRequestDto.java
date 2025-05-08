package com.example.Integrated.login.Dto.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocalRequestDto {

    private final String email;

    private final String password;
}

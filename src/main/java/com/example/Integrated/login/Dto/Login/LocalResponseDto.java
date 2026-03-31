package com.example.Integrated.login.Dto.Login;


import com.example.Integrated.login.Dto.User.UserDetailDto;
import com.example.Integrated.login.Dto.User.UserDto;
import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.login.Entity.User.UserDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LocalResponseDto {

    private final UserDto user;
    private final String jwt;
    private final UserDetailDto userDetail;
}
